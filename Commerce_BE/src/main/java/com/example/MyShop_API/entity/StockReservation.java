package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "stock_reservations", indexes = {@Index(name = "idx_stock_reservations_order_id", columnList = "order_id"), @Index(name = "idx_stock_reservations_variant_id", columnList = "variant_id"), @Index(name = "idx_stock_reservations_product_id", columnList = "product_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class StockReservation extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private Long variantId;
    private Long productId;
    private Integer reservedQuantity;
    private String status;
    private Instant expiresAt;
    @Version
    private Long version;


}
