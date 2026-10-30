package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "shipments", indexes = {@Index(name = "idx_shipments_seller_order_id", columnList = "seller_order_id"), @Index(name = "idx_shipments_shop_id", columnList = "shop_id"), @Index(name = "idx_shipments_warehouse_id", columnList = "warehouse_id"), @Index(name = "idx_shipments_assigned_shipper_id", columnList = "assigned_shipper_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long sellerOrderId;
    private Long shopId;
    private Long warehouseId;
    private Long assignedShipperId;
    private String trackingNo;
    private String carrier;
    @Builder.Default
    private String status = "PENDING";
    private Instant shippedAt;
    private Instant deliveredAt;
    @Version
    private Long version;
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
