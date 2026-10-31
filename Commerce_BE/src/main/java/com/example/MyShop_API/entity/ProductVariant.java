package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "product_variants", indexes = {@Index(name = "idx_product_variants_product_id", columnList = "product_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant extends AuditableEntity {
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
    private BigDecimal salePrice;
    private String currencyCode;
    private String status;
    @Version
    private Long version;


}
