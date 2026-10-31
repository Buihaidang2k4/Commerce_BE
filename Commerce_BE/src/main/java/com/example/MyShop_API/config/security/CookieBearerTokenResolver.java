package com.example.MyShop_API.config.security;

import com.example.MyShop_API.constants.SecurityWhitelistConstants;

import com.example.MyShop_API.constants.ApiPathConstants;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class CookieBearerTokenResolver implements BearerTokenResolver {
    private final String cookieName;
    private final List<String> publicPaths;
    private final AntPathMatcher matcher = new AntPathMatcher();

    public CookieBearerTokenResolver(String cookieName, String apiPrefix) {
        this.cookieName = cookieName;
        this.publicPaths = Stream.concat(SecurityWhitelistConstants.PUBLIC_ENDPOINTS.stream().map(path -> ApiPathConstants.resolve(apiPrefix, path)),
                SecurityWhitelistConstants.SWAGGER_ENDPOINTS.stream()).toList();
    }

    @Override
    public String resolve(HttpServletRequest request) {
        // Preserve the original error instead of validating an old cookie again on /error.
        if (request.getDispatcherType() == DispatcherType.ERROR) return null;
        String path = request.getServletPath();
        if (publicPaths.stream().anyMatch(pattern -> matcher.match(pattern, path))) return null;
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies()).filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue).filter(StringUtils::hasText).findFirst().orElse(null);
    }
}
