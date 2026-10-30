package com.example.MyShop_API.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OrderStatus {
    public static final String CREATED = "CREATED";
    public static final String PENDING = "PENDING";
    public static final String SHIPPED = "SHIPPED";
    public static final String DELIVERED = "DELIVERED";
    public static final String CANCELLED = "CANCELLED";
    public static final String RETURNED = "RETURNED";
    public static final Set<String> VALUES = Set.of(CREATED, PENDING, SHIPPED, DELIVERED, CANCELLED, RETURNED);

    public static String valueOf(String value) {
        if (!VALUES.contains(value)) throw new IllegalArgumentException("Invalid OrderStatus: " + value);
        return value;
    }
}
