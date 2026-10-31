package com.example.MyShop_API.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RoleCodeConstants {
    public static final String USER = "USER";
    public static final String ADMIN = "ADMIN";
    public static final Set<String> VALUES = Set.of(USER, ADMIN);

    public static String valueOf(String value) {
        if (!VALUES.contains(value)) throw new IllegalArgumentException("Invalid RoleCodeConstants: " + value);
        return value;
    }
}
