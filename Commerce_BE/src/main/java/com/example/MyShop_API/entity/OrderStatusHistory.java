package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "order_status_history", indexes = {@Index(name = "idx_order_status_history_order_id", columnList = "order_id"), @Index(name = "idx_order_status_history_changed_by_user_id", columnList = "changed_by_user_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;
    private Long orderId;
    private String fromStatus;
    private String orderStatus;
    private Long changedByUserId;
    private String reason;
    @Builder.Default
    private LocalDateTime changedAt = LocalDateTime.now();

}
