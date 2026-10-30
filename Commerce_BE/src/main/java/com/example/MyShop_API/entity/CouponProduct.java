package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "coupon_products", indexes = {@Index(name = "idx_coupon_products_coupon_id", columnList = "coupon_id"), @Index(name = "idx_coupon_products_product_id", columnList = "product_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long couponId;
    private Long productId;

}
