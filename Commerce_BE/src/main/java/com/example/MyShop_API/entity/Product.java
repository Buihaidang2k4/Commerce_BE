package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "products", indexes = {@Index(name = "idx_products_shop_id", columnList = "shop_id"), @Index(name = "idx_products_category_id", columnList = "category_id"), @Index(name = "idx_products_slug", columnList = "slug")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Product extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long shopId;
    private Long categoryId;
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(precision = 18, scale = 2)
    private BigDecimal price;
    @Column(precision = 18, scale = 2)
    private BigDecimal specialPrice;
    @Column(precision = 18, scale = 2)
    private BigDecimal discount;
    @Column(columnDefinition = "TEXT")
    private String shortDescription;
    private String slug;
    private Double height;
    private Double length;
    private Double weight;
    private Double width;
    private String origin;
    private Integer soldQuantity;
    private Integer reviewCount;
    private Double averageRating;
    private String status;
    @Version
    private Long version;


}
