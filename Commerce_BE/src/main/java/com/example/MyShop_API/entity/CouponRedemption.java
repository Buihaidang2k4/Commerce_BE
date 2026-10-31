package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "coupon_redemptions", indexes = {@Index(name = "idx_coupon_redemptions_coupon_id", columnList = "coupon_id"), @Index(name = "idx_coupon_redemptions_user_id", columnList = "user_id"), @Index(name = "idx_coupon_redemptions_order_id", columnList = "order_id"), @Index(name = "idx_coupon_redemptions_shop_id", columnList = "shop_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CouponRedemption extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long couponId;
    private Long userId;
    private Long orderId;
    private Long shopId;
    private String status;
    @Column(precision = 18, scale = 2)
    private BigDecimal discountAmount;
    private Instant expiresAt;


}
