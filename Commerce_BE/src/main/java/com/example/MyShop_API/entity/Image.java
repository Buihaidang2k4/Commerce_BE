package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "image", indexes = {@Index(name = "idx_image_product_id", columnList = "product_id"), @Index(name = "idx_image_variant_id", columnList = "variant_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private Long variantId;
    private String fileName;
    private String fileType;
    @Lob
    private Blob image;
    private String downloadUrl;
    private String url;
    private String objectKey;
    @Builder.Default
    private Integer sortOrder = 0;

}
