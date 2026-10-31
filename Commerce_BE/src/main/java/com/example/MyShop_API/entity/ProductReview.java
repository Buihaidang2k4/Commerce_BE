package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "product_reviews", indexes = {@Index(name = "idx_product_reviews_product_id", columnList = "product_id"), @Index(name = "idx_product_reviews_user_profile_id", columnList = "user_profile_id"), @Index(name = "idx_product_reviews_user_id", columnList = "user_id"), @Index(name = "idx_product_reviews_order_id", columnList = "order_id"), @Index(name = "idx_product_reviews_order_item_id", columnList = "order_item_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReview extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private Long userProfileId;
    private Long userId;
    private Long orderId;
    private Long orderItemId;
    private String reviewerNameSnapshot;
    private int rating;
    @Column(columnDefinition = "TEXT")
    private String content;
    private String moderationStatus;
    private boolean deleted;


}
