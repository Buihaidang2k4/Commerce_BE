package com.example.MyShop_API.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCouponRequest {
    Boolean enabled;
    BigDecimal maxDiscountAmount;
    BigDecimal minOrderValue;
    Instant startDate;
    Instant expiryDate;
}
