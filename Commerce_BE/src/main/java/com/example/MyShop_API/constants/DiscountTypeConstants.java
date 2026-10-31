package com.example.MyShop_API.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DiscountTypeConstants {
    public static final String PERCENTAGE = "PERCENTAGE";
    public static final String FIXED_AMOUNT = "FIXED_AMOUNT";
    public static final Set<String> VALUES = Set.of(PERCENTAGE);

    public static String valueOf(String value) {
        if (!VALUES.contains(value)) throw new IllegalArgumentException("Invalid DiscountTypeConstants: " + value);
        return value;
    }
}
