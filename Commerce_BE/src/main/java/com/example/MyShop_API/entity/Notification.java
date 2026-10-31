package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "notifications", indexes = {@Index(name = "idx_notifications_user_id", columnList = "user_id"), @Index(name = "idx_notifications_source_event_id", columnList = "source_event_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String channelCode;
    private String templateCode;
    private String subject;
    @Column(columnDefinition = "TEXT")
    private String body;
    private String status;
    private String sourceEventId;
    private Instant sentAt;
    private Instant readAt;


}
