package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "stock_reservations", indexes = {@Index(name = "idx_stock_reservations_order_id", columnList = "order_id"), @Index(name = "idx_stock_reservations_variant_id", columnList = "variant_id"), @Index(name = "idx_stock_reservations_product_id", columnList = "product_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private Long variantId;
    private Long productId;
    private Integer quantity;
    @Builder.Default
    private String status = "HELD";
    private Instant expiresAt;
    @Version
    private Long version;
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
