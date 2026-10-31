package com.example.MyShop_API.utils;

import com.example.MyShop_API.constants.AuthConstants;

import com.example.MyShop_API.constants.CommonMessageConstants;
import com.example.MyShop_API.exception.AppException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Locale;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthUtils {

    public static void validateCheckAuth(Collection<String> allowedRoles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new AppException(CommonMessageConstants.UNAUTHENTICATED);
        }
        boolean allowed = allowedRoles != null && authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .filter(authority -> authority != null && authority.startsWith(AuthConstants.ROLE_PREFIX))
                .map(AuthUtils::normalizeRole)
                .filter(role -> role != null)
                .anyMatch(role -> allowedRoles.stream().map(AuthUtils::normalizeRole).anyMatch(role::equals));
        if (!allowed) {
            throw new AppException(CommonMessageConstants.FORBIDDEN);
        }
    }

    /**
     * Requires the current role to belong to the allowed roles.
     * The current role must come from the authenticated account, not request data.
     * Null, empty or invalid roles are denied; ADMIN has no implicit bypass.
     */
    public static void validateCheckAuth(Collection<String> allowedRoles, String currentRole) {
        String role = normalizeRole(currentRole);
        if (role == null || allowedRoles == null || allowedRoles.isEmpty()
                || allowedRoles.stream().map(AuthUtils::normalizeRole).noneMatch(role::equals)) {
            throw new AppException(CommonMessageConstants.FORBIDDEN);
        }
    }

    private static String normalizeRole(String value) {
        if (value == null) return null;
        String role = value.trim().toUpperCase(Locale.ROOT);
        if (role.startsWith(AuthConstants.ROLE_PREFIX)) role = role.substring(AuthConstants.ROLE_PREFIX.length());
        return !role.startsWith(AuthConstants.ROLE_PREFIX) && role.matches(AuthConstants.ROLE_CODE_PATTERN) ? role : null;
    }
}
