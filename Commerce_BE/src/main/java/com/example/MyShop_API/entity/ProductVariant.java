package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product_variants", indexes = {@Index(name = "idx_product_variants_product_id", columnList = "product_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private String sku;
    @Column(columnDefinition = "TEXT")
    private String attributesJson;
    @Column(precision = 18, scale = 2)
    private BigDecimal price;
    @Column(precision = 18, scale = 2)
    private BigDecimal specialPrice;
    @Builder.Default
    private String currency = "VND";
    @Builder.Default
    private String status = "ACTIVE";
    @Version
    private Long version;
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
