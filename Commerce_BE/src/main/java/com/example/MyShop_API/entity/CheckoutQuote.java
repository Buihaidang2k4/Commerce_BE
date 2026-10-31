package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "checkout_quotes", indexes = {@Index(name = "idx_checkout_quotes_user_id", columnList = "user_id"), @Index(name = "idx_checkout_quotes_address_id", columnList = "address_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutQuote extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long addressId;
    private String currencyCode;
    @Column(precision = 18, scale = 2)
    private BigDecimal subtotalAmount;
    @Column(precision = 18, scale = 2)
    private BigDecimal discountAmount;
    @Column(precision = 18, scale = 2)
    private BigDecimal shippingAmount;
    @Column(precision = 18, scale = 2)
    private BigDecimal totalAmount;
    @Column(columnDefinition = "TEXT")
    private String snapshotJson;
    private Instant expiresAt;
    private String status;


}
