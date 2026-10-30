package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "coupon_redemptions", indexes = {@Index(name = "idx_coupon_redemptions_coupon_id", columnList = "coupon_id"), @Index(name = "idx_coupon_redemptions_user_id", columnList = "user_id"), @Index(name = "idx_coupon_redemptions_order_id", columnList = "order_id"), @Index(name = "idx_coupon_redemptions_shop_id", columnList = "shop_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponRedemption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long couponId;
    private Long userId;
    private Long orderId;
    private Long shopId;
    @Builder.Default
    private String status = "HELD";
    @Column(precision = 18, scale = 2)
    private BigDecimal discountAmount;
    private Instant expiresAt;
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
