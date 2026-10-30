package com.example.MyShop_API.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CouponScope {
    public static final String GLOBAL = "GLOBAL";
    public static final String CATEGORY = "CATEGORY";
    public static final String PRODUCT = "PRODUCT";
    public static final Set<String> VALUES = Set.of(GLOBAL, CATEGORY, PRODUCT);

    public static String valueOf(String value) {
        if (!VALUES.contains(value)) throw new IllegalArgumentException("Invalid CouponScope: " + value);
        return value;
    }
}
