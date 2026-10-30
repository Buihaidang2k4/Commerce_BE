package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "checkout_quotes", indexes = {@Index(name = "idx_checkout_quotes_user_id", columnList = "user_id"), @Index(name = "idx_checkout_quotes_address_id", columnList = "address_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutQuote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long addressId;
    @Builder.Default
    private String currency = "VND";
    @Column(precision = 18, scale = 2)
    private BigDecimal subtotal;
    @Column(precision = 18, scale = 2)
    private BigDecimal discountTotal;
    @Column(precision = 18, scale = 2)
    private BigDecimal shippingTotal;
    @Column(precision = 18, scale = 2)
    private BigDecimal grandTotal;
    @Column(columnDefinition = "TEXT")
    private String snapshotJson;
    private Instant expiresAt;
    @Builder.Default
    private String status = "ACTIVE";
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
