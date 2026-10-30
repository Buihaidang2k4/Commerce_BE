package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "payment_transactions", indexes = {@Index(name = "idx_payment_transactions_payment_id", columnList = "payment_id"), @Index(name = "idx_payment_transactions_provider_event_id", columnList = "provider_event_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long paymentId;
    private String provider;
    private String providerEventId;
    private String providerRef;
    @Column(precision = 18, scale = 2)
    private BigDecimal amount;
    @Builder.Default
    private String currency = "VND";
    private String status;
    @Column(columnDefinition = "TEXT")
    private String payloadHash;
    private Instant receivedAt;
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
