package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "support_tickets", indexes = {@Index(name = "idx_support_tickets_user_id", columnList = "user_id"), @Index(name = "idx_support_tickets_order_id", columnList = "order_id"), @Index(name = "idx_support_tickets_assigned_to_user_id", columnList = "assigned_to_user_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicket extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long orderId;
    private Long assignedToUserId;
    private String subject;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String status;
    private String priorityCode;


}
