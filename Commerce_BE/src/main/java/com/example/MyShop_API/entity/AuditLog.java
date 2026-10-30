package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "audit_logs", indexes = {@Index(name = "idx_audit_logs_actor_id", columnList = "actor_id"), @Index(name = "idx_audit_logs_resource_id", columnList = "resource_id"), @Index(name = "idx_audit_logs_correlation_id", columnList = "correlation_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long actorId;
    private String action;
    private String resource;
    private String resourceId;
    @Column(columnDefinition = "TEXT")
    private String beforeJson;
    @Column(columnDefinition = "TEXT")
    private String afterJson;
    private String correlationId;
    private String clientIp;
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
