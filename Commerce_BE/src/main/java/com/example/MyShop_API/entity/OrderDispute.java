package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "order_disputes", indexes = {@Index(name = "idx_order_disputes_order_id", columnList = "order_id"), @Index(name = "idx_order_disputes_shop_order_id", columnList = "shop_order_id"), @Index(name = "idx_order_disputes_return_request_id", columnList = "return_request_id"), @Index(name = "idx_order_disputes_opened_by_user_id", columnList = "opened_by_user_id"), @Index(name = "idx_order_disputes_assigned_to_user_id", columnList = "assigned_to_user_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDispute extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private Long shopOrderId;
    private Long returnRequestId;
    private Long openedByUserId;
    private Long assignedToUserId;
    private String reason;
    private String resolutionSummary;
    private String status;


}
