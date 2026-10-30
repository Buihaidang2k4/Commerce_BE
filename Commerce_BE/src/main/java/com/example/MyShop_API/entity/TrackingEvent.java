package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tracking_events", indexes = {@Index(name = "idx_tracking_events_shipment_id", columnList = "shipment_id"), @Index(name = "idx_tracking_events_actor_id", columnList = "actor_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long shipmentId;
    private String status;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String location;
    private Long actorId;
    private Instant occurredAt;
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
