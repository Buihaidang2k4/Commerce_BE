package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "return_request_items", indexes = {@Index(name = "idx_return_request_items_return_request_id", columnList = "return_request_id"), @Index(name = "idx_return_request_items_order_item_id", columnList = "order_item_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnRequestItem extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long returnRequestId;
    private Long orderItemId;
    private Integer requestedQuantity;
    private String reason;
    @Column(precision = 18, scale = 2)
    private BigDecimal requestedRefundAmount;


}
