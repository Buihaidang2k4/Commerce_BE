package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "outbox_events", indexes = {@Index(name = "idx_outbox_events_event_id", columnList = "event_id"), @Index(name = "idx_outbox_events_aggregate_id", columnList = "aggregate_id"), @Index(name = "idx_outbox_events_correlation_id", columnList = "correlation_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String eventId;
    private String aggregateType;
    private String aggregateId;
    private String eventType;
    private Long aggregateVersion;
    @Column(columnDefinition = "TEXT")
    private String payload;
    private String correlationId;
    private Instant occurredAt;
    private Instant publishedAt;
    @Builder.Default
    private Integer attempts = 0;
    @Column(columnDefinition = "TEXT")
    private String lastError;
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
