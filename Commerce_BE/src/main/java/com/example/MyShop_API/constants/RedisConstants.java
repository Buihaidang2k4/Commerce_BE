package com.example.MyShop_API.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RedisConstants {
    public static final String AUTH_RATE_STORE_REDIS = "redis";
    public static final String AUTH_RATE_STORE_MEMORY = "memory";
    public static final int AUTH_RATE_MAX_LOCAL_KEYS = 10_000;
    public static final String AUTH_RATE_PREFIX = "auth-rate:";
    public static final String AUTH_RATE_INCREMENT_SCRIPT =
            "local n = redis.call('INCR', KEYS[1]); if n == 1 then redis.call('EXPIRE', KEYS[1], ARGV[1]) end; return n";
}
