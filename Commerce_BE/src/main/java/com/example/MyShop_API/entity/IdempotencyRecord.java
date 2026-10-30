package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "idempotency_records", indexes = {@Index(name = "idx_idempotency_records_user_id", columnList = "user_id"), @Index(name = "idx_idempotency_records_idempotency_key", columnList = "idempotency_key")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdempotencyRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String operation;
    private String idempotencyKey;
    private String requestHash;
    private String status;
    private Integer responseStatus;
    @Column(columnDefinition = "TEXT")
    private String responseBody;
    private Instant expiresAt;
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
