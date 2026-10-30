package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "support_tickets", indexes = {@Index(name = "idx_support_tickets_user_id", columnList = "user_id"), @Index(name = "idx_support_tickets_order_id", columnList = "order_id"), @Index(name = "idx_support_tickets_assigned_user_id", columnList = "assigned_user_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long orderId;
    private Long assignedUserId;
    private String subject;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Builder.Default
    private String status = "OPEN";
    private String priority;
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
