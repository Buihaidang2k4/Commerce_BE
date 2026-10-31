package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "orders", indexes = {@Index(name = "idx_orders_user_id", columnList = "user_id"), @Index(name = "idx_orders_user_profile_id", columnList = "user_profile_id"), @Index(name = "idx_orders_payment_id", columnList = "payment_id"), @Index(name = "idx_orders_coupon_id", columnList = "coupon_id"), @Index(name = "idx_orders_idempotency_key", columnList = "idempotency_key")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Order extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long userProfileId;
    private Long paymentId;
    private Long couponId;
    private String orderNumber;
    private String idempotencyKey;
    private String requestHash;
    private String currencyCode;
    private LocalDate orderDate;
    @Column(precision = 18, scale = 2)
    private BigDecimal subtotalAmount;
    @Column(precision = 18, scale = 2)
    private BigDecimal shippingAmount;
    @Column(precision = 18, scale = 2)
    private BigDecimal discountAmount;
    @Column(precision = 18, scale = 2)
    private BigDecimal totalAmount;
    private String status;
    @Version
    private Long version;


}
