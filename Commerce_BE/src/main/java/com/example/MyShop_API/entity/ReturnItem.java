package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "return_items", indexes = {@Index(name = "idx_return_items_return_request_id", columnList = "return_request_id"), @Index(name = "idx_return_items_order_item_id", columnList = "order_item_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long returnRequestId;
    private Long orderItemId;
    private Integer quantity;
    @Column(columnDefinition = "TEXT")
    private String reason;
    @Column(precision = 18, scale = 2)
    private BigDecimal requestedRefundAmount;
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
