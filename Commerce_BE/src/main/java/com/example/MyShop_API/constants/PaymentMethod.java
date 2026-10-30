package com.example.MyShop_API.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaymentMethod {
    public static final String VNPAY = "VNPAY";
    public static final String MOMO = "MOMO";
    public static final String CASH = "CASH";
    public static final Set<String> VALUES = Set.of(VNPAY, MOMO, CASH);

    public static String valueOf(String value) {
        if (!VALUES.contains(value)) throw new IllegalArgumentException("Invalid PaymentMethod: " + value);
        return value;
    }
}
