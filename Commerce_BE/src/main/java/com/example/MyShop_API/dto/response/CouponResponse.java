package com.example.MyShop_API.dto.response;

import com.example.MyShop_API.constants.CouponScope;
import com.example.MyShop_API.constants.DiscountType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CouponResponse {
    Long couponId;
    String code;
    String scope;
    String discountType;
    BigDecimal discountPercent; // 10.00 = 10%
    BigDecimal discountAmount; // 5000VNĐ
    BigDecimal maxDiscountAmount; // maximum discount limit
    BigDecimal minOrderValue; // Minimum conditions for discount
    Instant startDate;
    Instant expiryDate;
    List<Long> categoryIds = new ArrayList<>();
    List<Long> productIds = new ArrayList<>();
    boolean enabled = true;
}
