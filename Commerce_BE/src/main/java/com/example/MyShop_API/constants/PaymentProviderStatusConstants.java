package com.example.MyShop_API.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaymentProviderStatusConstants {
    public static final String PENDING = "PENDING";
    public static final String SUCCESS = "SUCCESS";
    public static final String EXPIRED = "EXPIRED";
}
