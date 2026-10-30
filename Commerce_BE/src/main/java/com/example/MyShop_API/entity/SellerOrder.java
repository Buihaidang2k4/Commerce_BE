package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "seller_orders", indexes = {@Index(name = "idx_seller_orders_order_id", columnList = "order_id"), @Index(name = "idx_seller_orders_shop_id", columnList = "shop_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private Long shopId;
    @Builder.Default
    private String status = "PENDING_PAYMENT";
    @Column(precision = 18, scale = 2)
    private BigDecimal subtotal;
    @Column(precision = 18, scale = 2)
    private BigDecimal shippingFee;
    @Column(precision = 18, scale = 2)
    private BigDecimal discountTotal;
    @Column(precision = 18, scale = 2)
    private BigDecimal grandTotal;
    @Version
    private Long version;
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
