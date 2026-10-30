package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "cart_items", indexes = {@Index(name = "idx_cart_items_cart_id", columnList = "cart_id"), @Index(name = "idx_cart_items_product_id", columnList = "product_id"), @Index(name = "idx_cart_items_variant_id", columnList = "variant_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;
    private Long cartId;
    private Long productId;
    private Long variantId;
    private Integer quantity;
    @Column(precision = 18, scale = 2)
    private BigDecimal unitPrice;
    @Column(precision = 18, scale = 2)
    private BigDecimal totalPrice;
    public void setTotalPrice() {
        totalPrice = unitPrice == null || quantity == null ? BigDecimal.ZERO : unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
