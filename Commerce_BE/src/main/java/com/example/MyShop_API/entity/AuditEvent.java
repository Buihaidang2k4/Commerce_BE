package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "audit_events", indexes = {@Index(name = "idx_audit_events_actor_user_id", columnList = "actor_user_id"), @Index(name = "idx_audit_events_resource_id", columnList = "resource_id"), @Index(name = "idx_audit_events_correlation_id", columnList = "correlation_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEvent extends CreatedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long actorUserId;
    private String actionCode;
    private String resourceType;
    private String resourceId;
    @Column(columnDefinition = "TEXT")
    private String beforeJson;
    @Column(columnDefinition = "TEXT")
    private String afterJson;
    private String correlationId;
    private String clientIp;


}
