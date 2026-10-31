package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "order_items", indexes = {@Index(name = "idx_order_items_order_id", columnList = "order_id"), @Index(name = "idx_order_items_shop_order_id", columnList = "shop_order_id"), @Index(name = "idx_order_items_product_id", columnList = "product_id"), @Index(name = "idx_order_items_variant_id", columnList = "variant_id"), @Index(name = "idx_order_items_cart_item_id", columnList = "cart_item_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private Long shopOrderId;
    private Long productId;
    private Long variantId;
    private Long cartItemId;
    private String productNameSnapshot;
    private String skuSnapshot;
    @Column(columnDefinition = "TEXT")
    private String variantAttributesSnapshot;
    private Integer quantity;
    @Column(precision = 18, scale = 2)
    private BigDecimal unitPrice;
    @Column(precision = 18, scale = 2)
    private BigDecimal discountAmount;


}
