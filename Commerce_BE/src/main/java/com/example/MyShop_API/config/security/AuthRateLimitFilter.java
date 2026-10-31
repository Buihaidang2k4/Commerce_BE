package com.example.MyShop_API.config.security;

import com.example.MyShop_API.constants.RedisConstants;
import com.example.MyShop_API.constants.CommonConstants;

import com.example.MyShop_API.constants.ApiPathConstants;
import com.example.MyShop_API.constants.CommonMessageConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.MyShop_API.exception.MessageHandlerUtils;
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
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;

/** Shared fixed-window limits; only trust the direct peer IP. */
@RequiredArgsConstructor
@Slf4j
public class AuthRateLimitFilter extends OncePerRequestFilter {
    private static final DefaultRedisScript<Long> INCREMENT = new DefaultRedisScript<>(
            RedisConstants.AUTH_RATE_INCREMENT_SCRIPT, Long.class);
    private final StringRedisTemplate redis;
    private final String prefix;
    private final int limit;
    private final int windowSeconds;
    private final String store;
    private final Map<String, LocalWindow> localWindows = new HashMap<>();

    private record LocalWindow(long startedNanos, long count) {}


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String path = request.getServletPath();
        boolean limited = CommonConstants.AUTH_RATE_LIMIT_METHODS.contains(request.getMethod()) &&
                Set.of(ApiPathConstants.resolve(prefix, ApiPathConstants.BASE_AUTH) + ApiPathConstants.AUTH_LOGIN,
                        ApiPathConstants.resolve(prefix, ApiPathConstants.PUBLIC_USERS_REGISTRATION),
                        ApiPathConstants.resolve(prefix, ApiPathConstants.BASE_AUTH) + ApiPathConstants.AUTH_FORGOT_PASSWORD,
                        ApiPathConstants.resolve(prefix, ApiPathConstants.BASE_AUTH) + ApiPathConstants.AUTH_RESET_PASSWORD).contains(path);
        if (limited) {
            try {
                String key = RedisConstants.AUTH_RATE_PREFIX + path + ":" + request.getRemoteAddr();
                Long count = RedisConstants.AUTH_RATE_STORE_MEMORY.equals(store)
                        ? incrementLocal(key)
                        : redis.execute(INCREMENT, List.of(key), String.valueOf(windowSeconds));
                if (count == null) throw new IllegalStateException("No rate limit result");
                if (count > limit) {
                    response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(windowSeconds));
                    SecurityErrorWriter.write(request, response, 429, 429, CommonMessageConstants.RATE_LIMIT_EXCEEDED);
                    return;
                }
            } catch (DataAccessException | IllegalStateException ex) {
                log.warn("{} method={} path={} cause={}",
                        MessageHandlerUtils.getMessage(CommonMessageConstants.AUTH_UNAVAILABLE),
                        request.getMethod(), path, ex.getClass().getSimpleName());
                SecurityErrorWriter.write(request, response, 503, 503, CommonMessageConstants.AUTH_UNAVAILABLE);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private long incrementLocal(String key) {
        long now = System.nanoTime();
        long duration = TimeUnit.SECONDS.toNanos(windowSeconds);
        synchronized (localWindows) {
            LocalWindow current = localWindows.get(key);
            if (current == null || now - current.startedNanos() >= duration) {
                if (current == null && localWindows.size() >= RedisConstants.AUTH_RATE_MAX_LOCAL_KEYS) {
                    localWindows.entrySet().removeIf(entry -> now - entry.getValue().startedNanos() >= duration);
                    if (localWindows.size() >= RedisConstants.AUTH_RATE_MAX_LOCAL_KEYS) return (long) limit + 1;
                }
                localWindows.put(key, new LocalWindow(now, 1));
                return 1;
            }
            long count = Math.min((long) limit + 1, current.count() + 1);
            localWindows.put(key, new LocalWindow(current.startedNanos(), count));
            return count;
        }
    }
}
