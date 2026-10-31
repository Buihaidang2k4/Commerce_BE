package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "refunds", indexes = {@Index(name = "idx_refunds_payment_intent_id", columnList = "payment_intent_id"), @Index(name = "idx_refunds_order_id", columnList = "order_id"), @Index(name = "idx_refunds_return_request_id", columnList = "return_request_id"), @Index(name = "idx_refunds_idempotency_key", columnList = "idempotency_key"), @Index(name = "idx_refunds_requested_by_user_id", columnList = "requested_by_user_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Refund extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long paymentIntentId;
    private Long orderId;
    private Long returnRequestId;
    @Column(precision = 18, scale = 2)
    private BigDecimal amount;
    private String currencyCode;
    private String status;
    private String reason;
    private String idempotencyKey;
    private Long requestedByUserId;
    @Version
    private Long version;


}
