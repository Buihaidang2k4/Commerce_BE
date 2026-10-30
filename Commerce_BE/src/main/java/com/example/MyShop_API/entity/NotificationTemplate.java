package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "notification_templates", indexes = {@Index(name = "idx_notification_templates_code", columnList = "code")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String locale;
    private String channel;
    private String subjectTemplate;
    @Column(columnDefinition = "TEXT")
    private String bodyTemplate;
    @Builder.Default
    private Boolean enabled = true;
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
