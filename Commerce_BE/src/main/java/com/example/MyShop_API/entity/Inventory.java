package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "inventory", indexes = {@Index(name = "idx_inventory_product_id", columnList = "product_id"), @Index(name = "idx_inventory_variant_id", columnList = "variant_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;
    private Long productId;
    private Long variantId;
    @Builder.Default
    private int available = 0;
    @Builder.Default
    private int reserved = 0;
    private LocalDateTime updatedAt;
    @Version
    private Long version;
    public boolean canReserve(int quantity) { return quantity > 0 && available >= quantity; }
}
