package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "shipments", indexes = {@Index(name = "idx_shipments_shop_order_id", columnList = "shop_order_id"), @Index(name = "idx_shipments_shop_id", columnList = "shop_id"), @Index(name = "idx_shipments_warehouse_id", columnList = "warehouse_id"), @Index(name = "idx_shipments_assigned_shipper_id", columnList = "assigned_shipper_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Shipment extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long shopOrderId;
    private Long shopId;
    private Long warehouseId;
    private Long assignedShipperId;
    private String trackingNumber;
    private String carrierCode;
    private String status;
    private Instant shippedAt;
    private Instant deliveredAt;
    @Version
    private Long version;


}
