package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "product_embeddings", indexes = {@Index(name = "idx_product_embeddings_product_id", columnList = "product_id"), @Index(name = "idx_product_embeddings_variant_id", columnList = "variant_id"), @Index(name = "idx_product_embeddings_vector_id", columnList = "vector_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEmbedding extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private Long variantId;
    private String modelName;
    private String vectorId;
    private String sourceHash;
    private String status;


}
