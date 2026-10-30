package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "refunds", indexes = {@Index(name = "idx_refunds_payment_id", columnList = "payment_id"), @Index(name = "idx_refunds_order_id", columnList = "order_id"), @Index(name = "idx_refunds_return_request_id", columnList = "return_request_id"), @Index(name = "idx_refunds_idempotency_key", columnList = "idempotency_key"), @Index(name = "idx_refunds_actor_id", columnList = "actor_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Refund {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long paymentId;
    private Long orderId;
    private Long returnRequestId;
    @Column(precision = 18, scale = 2)
    private BigDecimal amount;
    @Builder.Default
    private String currency = "VND";
    @Builder.Default
    private String status = "REQUESTED";
    @Column(columnDefinition = "TEXT")
    private String reason;
    private String idempotencyKey;
    private Long actorId;
    @Version
    private Long version;
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
