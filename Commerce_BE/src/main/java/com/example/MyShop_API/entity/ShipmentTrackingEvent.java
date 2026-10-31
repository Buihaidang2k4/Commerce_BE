package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "shipment_tracking_events", indexes = {@Index(name = "idx_shipment_tracking_events_shipment_id", columnList = "shipment_id"), @Index(name = "idx_shipment_tracking_events_actor_user_id", columnList = "actor_user_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentTrackingEvent extends CreatedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long shipmentId;
    private String shipmentStatus;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String location;
    private Long actorUserId;
    private Instant occurredAt;


}
