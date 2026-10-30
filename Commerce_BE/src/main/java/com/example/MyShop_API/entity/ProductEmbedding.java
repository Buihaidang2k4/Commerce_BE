package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product_embeddings", indexes = {@Index(name = "idx_product_embeddings_product_id", columnList = "product_id"), @Index(name = "idx_product_embeddings_variant_id", columnList = "variant_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEmbedding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private Long variantId;
    private String model;
    private String vectorReference;
    private String sourceHash;
    private String status;
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
