package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "idempotency_records", indexes = {@Index(name = "idx_idempotency_records_user_id", columnList = "user_id"), @Index(name = "idx_idempotency_records_idempotency_key", columnList = "idempotency_key")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class IdempotencyRecord extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String operationCode;
    private String idempotencyKey;
    private String requestHash;
    private String status;
    private Integer responseStatusCode;
    @Column(columnDefinition = "TEXT")
    private String responseBody;
    private Instant expiresAt;


}
