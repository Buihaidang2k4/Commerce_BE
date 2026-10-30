package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "flash_sale_purchases", indexes = {@Index(name = "idx_flash_sale_purchases_campaign_id", columnList = "campaign_id"), @Index(name = "idx_flash_sale_purchases_flash_sale_item_id", columnList = "flash_sale_item_id"), @Index(name = "idx_flash_sale_purchases_user_id", columnList = "user_id"), @Index(name = "idx_flash_sale_purchases_order_id", columnList = "order_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashSalePurchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long campaignId;
    private Long flashSaleItemId;
    private Long userId;
    private Long orderId;
    private Integer quantity;
    @Builder.Default
    private String status = "HELD";
    private Instant expiresAt;
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
