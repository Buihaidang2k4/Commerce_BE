package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "notifications", indexes = {@Index(name = "idx_notifications_user_id", columnList = "user_id"), @Index(name = "idx_notifications_event_id", columnList = "event_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String channel;
    private String templateCode;
    private String subject;
    @Column(columnDefinition = "TEXT")
    private String content;
    @Builder.Default
    private String status = "PENDING";
    private String eventId;
    private Instant sentAt;
    private Instant readAt;
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
