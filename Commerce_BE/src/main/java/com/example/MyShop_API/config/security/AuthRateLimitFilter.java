package com.example.MyShop_API.config.security;

import com.example.MyShop_API.constants.ApiPath;
import com.example.MyShop_API.constants.CommonMessageUtils;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/** Shared fixed-window limits; only trust the direct peer IP. */
@RequiredArgsConstructor
public class AuthRateLimitFilter extends OncePerRequestFilter {
    private static final DefaultRedisScript<Long> INCREMENT = new DefaultRedisScript<>(
            "local n = redis.call('INCR', KEYS[1]); if n == 1 then redis.call('EXPIRE', KEYS[1], ARGV[1]) end; return n", Long.class);
    private final StringRedisTemplate redis;
    private final String prefix;
    private final int limit;
    private final int windowSeconds;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String path = request.getServletPath();
        boolean limited = Set.of("POST", "PUT").contains(request.getMethod()) &&
                Set.of(ApiPath.resolve(prefix, ApiPath.BASE_AUTH) + ApiPath.AUTH_LOGIN,
                        ApiPath.resolve(prefix, ApiPath.BASE_AUTH) + ApiPath.AUTH_GOOGLE,
                        ApiPath.resolve(prefix, ApiPath.BASE_AUTH) + ApiPath.AUTH_FORGOT_PASSWORD,
                        ApiPath.resolve(prefix, ApiPath.BASE_AUTH) + ApiPath.AUTH_RESET_PASSWORD).contains(path);
        if (limited) {
            try {
                Long count = redis.execute(INCREMENT,
                        List.of("auth-rate:" + path + ":" + request.getRemoteAddr()), String.valueOf(windowSeconds));
                if (count == null) throw new IllegalStateException("No rate limit result");
                if (count > limit) {
                    response.setHeader("Retry-After", String.valueOf(windowSeconds));
                    SecurityErrorWriter.write(request, response, 429, 429, CommonMessageUtils.RATE_LIMIT_EXCEEDED);
                    return;
                }
            } catch (org.springframework.data.redis.RedisConnectionFailureException ex) {
                SecurityErrorWriter.write(request, response, 503, 503, CommonMessageUtils.AUTH_UNAVAILABLE);
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
