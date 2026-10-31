package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "payment_intents", indexes = {@Index(name = "idx_payment_intents_order_id", columnList = "order_id"), @Index(name = "idx_payment_intents_provider_transaction_id", columnList = "provider_transaction_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentIntent extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private String providerCode;
    private String providerReference;
    private String methodCode;
    private String status;
    private String vnpTxnRef;
    @Column(precision = 18, scale = 2)
    private BigDecimal amount;
    private String currencyCode;
    private String orderInfo;
    private String bankCode;
    private String responseCode;
    private String providerStatus;
    private Instant paymentDate;
    private Instant expiresAt;
    private String providerTransactionId;
    private String cardType;


}
