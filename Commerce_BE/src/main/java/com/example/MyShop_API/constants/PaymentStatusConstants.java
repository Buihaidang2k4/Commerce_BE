package com.example.MyShop_API.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaymentStatusConstants {
    public static final String INIT = "INIT";
    public static final String UNPAID = "UNPAID";
    public static final String PAID = "PAID";
    public static final String PENDING = "PENDING";
    public static final String FAILED = "FAILED";
    public static final String EXPIRED = "EXPIRED";
    public static final Set<String> VALUES = Set.of(INIT, UNPAID, PAID, PENDING, FAILED, EXPIRED);

    public static String valueOf(String value) {
        if (!VALUES.contains(value)) throw new IllegalArgumentException("Invalid PaymentStatusConstants: " + value);
        return value;
    }
}
