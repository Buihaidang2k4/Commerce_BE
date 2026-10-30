package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "search_documents", indexes = {@Index(name = "idx_search_documents_product_id", columnList = "product_id"), @Index(name = "idx_search_documents_variant_id", columnList = "variant_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private Long variantId;
    private String sourceHash;
    @Column(columnDefinition = "TEXT")
    private String content;
    private String status;
    private Instant indexedAt;
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
