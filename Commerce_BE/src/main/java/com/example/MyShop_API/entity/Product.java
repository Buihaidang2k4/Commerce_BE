package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "products", indexes = {@Index(name = "idx_products_shop_id", columnList = "shop_id"), @Index(name = "idx_products_category_id", columnList = "category_id"), @Index(name = "idx_products_slug", columnList = "slug")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    private Long shopId;
    private Long categoryId;
    private String productName;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(precision = 18, scale = 2)
    private BigDecimal price;
    @Column(precision = 18, scale = 2)
    private BigDecimal specialPrice;
    @Column(precision = 18, scale = 2)
    private BigDecimal discount;
    @Column(columnDefinition = "TEXT")
    private String bio;
    private String slug;
    private Double height;
    private Double length;
    private Double weight;
    private Double width;
    private String origin;
    @Builder.Default
    private Integer soldCount = 0;
    @Builder.Default
    private Integer reviewCount = 0;
    @Builder.Default
    private Double avgRating = 0.0;
    @Builder.Default
    private String status = "DRAFT";
    private LocalDate createAt;
    private LocalDate updateAt;
    @Version
    private Long version;

}
