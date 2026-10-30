package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "consumer_inbox", indexes = {@Index(name = "idx_consumer_inbox_event_id", columnList = "event_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsumerInbox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String eventId;
    private String consumerName;
    private Instant processedAt;
    private String status;
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
