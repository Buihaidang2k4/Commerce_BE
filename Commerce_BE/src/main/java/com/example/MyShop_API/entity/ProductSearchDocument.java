package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "product_search_documents", indexes = {@Index(name = "idx_product_search_documents_product_id", columnList = "product_id"), @Index(name = "idx_product_search_documents_variant_id", columnList = "variant_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchDocument extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private Long variantId;
    private String sourceHash;
    @Column(columnDefinition = "TEXT")
    private String searchableText;
    private String indexingStatus;
    private Instant indexedAt;


}
