package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "outbox_events", indexes = {@Index(name = "idx_outbox_events_event_id", columnList = "event_id"), @Index(name = "idx_outbox_events_aggregate_id", columnList = "aggregate_id"), @Index(name = "idx_outbox_events_correlation_id", columnList = "correlation_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String eventId;
    private String aggregateType;
    private String aggregateId;
    private String eventType;
    private Long aggregateVersion;
    @Column(columnDefinition = "TEXT")
    private String payloadJson;
    private String correlationId;
    private Instant occurredAt;
    private Instant publishedAt;
    private Integer publishAttemptCount;
    private String lastErrorMessage;


}
