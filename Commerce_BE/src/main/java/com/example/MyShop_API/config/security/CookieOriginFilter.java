package com.example.MyShop_API.config.security;

import com.example.MyShop_API.constants.CommonMessageUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/** Protect browser writes using ambient authentication cookies. */
public class CookieOriginFilter extends OncePerRequestFilter {
    private final List<String> allowedOrigins;

    public CookieOriginFilter(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        if (!Set.of("GET", "HEAD", "OPTIONS").contains(request.getMethod())) {
            String origin = request.getHeader("Origin");
            boolean hasCookie = request.getCookies() != null && Arrays.stream(request.getCookies())
                    .anyMatch(c -> Set.of("access_token", "refresh_token").contains(c.getName()));
            if (origin == null && hasCookie) {
                try {
                    URI referer = URI.create(request.getHeader("Referer"));
                    origin = referer.getScheme() + "://" + referer.getRawAuthority();
                } catch (Exception ignored) {
                    SecurityErrorWriter.write(request, response, 403, 403, CommonMessageUtils.ORIGIN_REQUIRED);
                    return;
                }
            }
            if (origin != null && !allowedOrigins.contains(origin)) {
                SecurityErrorWriter.write(request, response, 403, 403, CommonMessageUtils.ORIGIN_NOT_ALLOWED);
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
