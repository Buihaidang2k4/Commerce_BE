package com.example.MyShop_API.dto.response;

import com.example.MyShop_API.Enum.CouponScope;
import com.example.MyShop_API.Enum.DiscountType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    CouponScope scope;
    DiscountType discountType;
    BigDecimal discountPercent; // 10.00 = 10%
    BigDecimal discountAmount; // 5000VNĐ
    BigDecimal maxDiscountAmount; // maximum discount limit
    BigDecimal minOrderValue; // Minimum conditions for discount
    LocalDateTime startDate;
    LocalDateTime expiryDate;
    List<Long> categoryIds = new ArrayList<>();
    List<Long> productIds = new ArrayList<>();
    boolean enabled = true;
}
