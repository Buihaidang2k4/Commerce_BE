package com.example.MyShop_API.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CookieConstants {
    public static final String REFRESH_COOKIE_NAME = "refresh_token";
    public static final String ACCESS_COOKIE_NAME = "access_token";
    public static final String PATH = "/";
    public static final String SAME_SITE_LAX = "Lax";
    public static final String SAME_SITE_STRICT = "Strict";
    public static final String SAME_SITE_NONE = "None";
    public static final Set<String> AUTH_COOKIE_NAMES = Set.of(ACCESS_COOKIE_NAME, REFRESH_COOKIE_NAME);
    public static final Set<String> SAME_SITE_VALUES = Set.of(SAME_SITE_LAX, SAME_SITE_STRICT, SAME_SITE_NONE);
}
