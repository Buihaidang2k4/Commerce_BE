package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "coupon_categories", indexes = {@Index(name = "idx_coupon_categories_coupon_id", columnList = "coupon_id"), @Index(name = "idx_coupon_categories_category_id", columnList = "category_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long couponId;
    private Long categoryId;

}
