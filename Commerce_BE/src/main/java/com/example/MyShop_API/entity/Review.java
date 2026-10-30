package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "reviews", indexes = {@Index(name = "idx_reviews_product_id", columnList = "product_id"), @Index(name = "idx_reviews_profile_id", columnList = "profile_id"), @Index(name = "idx_reviews_user_id", columnList = "user_id"), @Index(name = "idx_reviews_order_id", columnList = "order_id"), @Index(name = "idx_reviews_order_item_id", columnList = "order_item_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;
    private Long productId;
    private Long profileId;
    private Long userId;
    private Long orderId;
    private Long orderItemId;
    private String customerName;
    private int rating;
    private String comment;
    @Builder.Default
    private String status = "PUBLISHED";
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default
    private boolean deleted = false;

}
