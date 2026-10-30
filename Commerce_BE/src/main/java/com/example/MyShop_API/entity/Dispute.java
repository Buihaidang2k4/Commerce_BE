package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "disputes", indexes = {@Index(name = "idx_disputes_order_id", columnList = "order_id"), @Index(name = "idx_disputes_seller_order_id", columnList = "seller_order_id"), @Index(name = "idx_disputes_return_request_id", columnList = "return_request_id"), @Index(name = "idx_disputes_opened_by_user_id", columnList = "opened_by_user_id"), @Index(name = "idx_disputes_assigned_user_id", columnList = "assigned_user_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dispute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private Long sellerOrderId;
    private Long returnRequestId;
    private Long openedByUserId;
    private Long assignedUserId;
    @Column(columnDefinition = "TEXT")
    private String reason;
    @Column(columnDefinition = "TEXT")
    private String resolution;
    @Builder.Default
    private String status = "OPEN";
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
