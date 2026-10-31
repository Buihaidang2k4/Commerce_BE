package com.example.MyShop_API.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthConstants {
    public static final String DEFAULT_ISSUER = "myshop-api";
    public static final String DEFAULT_AUDIENCE = "myshop-web";
    public static final String TOKEN_TYPE_CLAIM = "type";
    public static final String ACCESS_TOKEN_TYPE = "access_token";
    public static final String USER_ID_CLAIM = "uid";
    public static final String SESSION_ID_CLAIM = "sid";
    public static final String SCOPE_CLAIM = "scope";
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String ROLE_CODE_PATTERN = "[A-Z][A-Z0-9_]{1,99}";
    public static final String PERMISSION_CODE_PATTERN = "[A-Z][A-Z0-9_:.]{1,99}";
    public static final String REFRESH_TOKEN_PATTERN = "[A-Za-z0-9_-]{43}";
    public static final String AUTHORIZATION_ALGORITHM_HEADER = "alg";
    public static final String AUTHORIZATION_TYPE_HEADER = "typ";
    public static final int MIN_SECRET_BYTES = 32;
    public static final int REFRESH_TOKEN_BYTES = 32;
    public static final int MAX_ACCESS_TOKEN_LENGTH = 16384;
    public static final int MAX_LOGOUT_TOKEN_LENGTH = 512;
    public static final int CLOCK_SKEW_SECONDS = 30;
    public static final int SESSION_RETENTION_DAYS = 7;
    public static final String SESSION_CLEANUP_CRON = "0 0 3 * * *";
    public static final int OTP_LENGTH = 6;
    public static final int OTP_TTL_SECONDS = 300;
    public static final int OTP_RESEND_SECONDS = 60;
    public static final int OTP_MAX_ATTEMPTS = 5;
    public static final int OTP_RETENTION_SECONDS = 86400;
    public static final String OTP_CLEANUP_CRON = "0 30 3 * * *";
    public static final String EMAIL_PATTERN = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
    public static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    public static final String OTP_PATTERN = "[0-9]{6}";
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 72;
}
