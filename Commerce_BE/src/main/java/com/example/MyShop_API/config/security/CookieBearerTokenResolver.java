package com.example.MyShop_API.config.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * check if this api requires login (cookies)
 */
@RequiredArgsConstructor
public class CookieBearerTokenResolver implements BearerTokenResolver {
    private final String cookieName;

    @Override
    public String resolve(HttpServletRequest request) {
        String path = request.getRequestURI();

        // public endpoint -> pass
        if (isPublicEndpoint(path)) return null;

        // get token from cookies
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(c -> cookieName.equals(c.getName()))
                .map(Cookie::getValue)
                .filter(StringUtils::hasText)
                .findFirst()
                .orElse(null);
    }

    private boolean isPublicEndpoint(String path) {
        return SecurityWhitelist.PUBLIC_ENDPOINTS.stream()
                .anyMatch(patten -> matchPatten(patten, path))
                || SecurityWhitelist.SWAGGER_ENDPOINTS.stream()
                .anyMatch(patten -> matchPatten(patten, path));
    }

    // /* /** , exact
    private boolean matchPatten(String pattern, String path) {
        if (pattern.endsWith("/**")) {
            return path.startsWith(pattern.substring(0, pattern.length() - 3));
        }

        if (pattern.endsWith("/*"))
            return path.startsWith(pattern.substring(0, pattern.length() - 2));
        return path.equals(pattern);
    }
}
