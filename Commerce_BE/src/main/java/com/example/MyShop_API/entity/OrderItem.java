package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "order_items", indexes = {@Index(name = "idx_order_items_order_id", columnList = "order_id"), @Index(name = "idx_order_items_seller_order_id", columnList = "seller_order_id"), @Index(name = "idx_order_items_product_id", columnList = "product_id"), @Index(name = "idx_order_items_variant_id", columnList = "variant_id"), @Index(name = "idx_order_items_cart_item_id", columnList = "cart_item_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;
    private Long orderId;
    private Long sellerOrderId;
    private Long productId;
    private Long variantId;
    private Long cartItemId;
    private String productTitle;
    private String sku;
    @Column(columnDefinition = "TEXT")
    private String attributesSnapshot;
    private Integer quantity;
    @Column(precision = 18, scale = 2)
    private BigDecimal price;
    @Builder.Default
    @Column(precision = 18, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

}
