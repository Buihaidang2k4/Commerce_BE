package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "payment_transactions", indexes = {@Index(name = "idx_payment_transactions_payment_intent_id", columnList = "payment_intent_id"), @Index(name = "idx_payment_transactions_provider_event_id", columnList = "provider_event_id"), @Index(name = "idx_payment_transactions_provider_transaction_id", columnList = "provider_transaction_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long paymentIntentId;
    private String providerCode;
    private String providerEventId;
    private String providerTransactionId;
    @Column(precision = 18, scale = 2)
    private BigDecimal amount;
    private String currencyCode;
    private String transactionStatus;
    private String payloadHash;
    private Instant receivedAt;


}
