package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "orders", indexes = {@Index(name = "idx_orders_user_id", columnList = "user_id"), @Index(name = "idx_orders_profile_id", columnList = "profile_id"), @Index(name = "idx_orders_payment_id", columnList = "payment_id"), @Index(name = "idx_orders_coupon_id", columnList = "coupon_id"), @Index(name = "idx_orders_idempotency_key", columnList = "idempotency_key")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    private Long userId;
    private Long profileId;
    private Long paymentId;
    private Long couponId;
    private String orderNo;
    private String idempotencyKey;
    private String requestHash;
    @Builder.Default
    private String currency = "VND";
    private LocalDate orderDate;
    @Column(precision = 18, scale = 2)
    private BigDecimal subtotal;
    @Builder.Default
    @Column(precision = 18, scale = 2)
    private BigDecimal shippingFee = BigDecimal.ZERO;
    @Builder.Default
    @Column(precision = 18, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;
    @Column(precision = 18, scale = 2)
    private BigDecimal totalAmount;
    private String orderStatus;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
    @Version
    private Long version;

}
