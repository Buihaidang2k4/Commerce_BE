package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "stock_movements", indexes = {@Index(name = "idx_stock_movements_variant_id", columnList = "variant_id"), @Index(name = "idx_stock_movements_product_id", columnList = "product_id"), @Index(name = "idx_stock_movements_ref_id", columnList = "ref_id"), @Index(name = "idx_stock_movements_actor_id", columnList = "actor_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long variantId;
    private Long productId;
    private Integer quantityDelta;
    @Column(columnDefinition = "TEXT")
    private String reason;
    private String refType;
    private Long refId;
    private Long actorId;
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
