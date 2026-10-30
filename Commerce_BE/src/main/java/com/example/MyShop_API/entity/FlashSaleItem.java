package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "flash_sale_items", indexes = {@Index(name = "idx_flash_sale_items_campaign_id", columnList = "campaign_id"), @Index(name = "idx_flash_sale_items_variant_id", columnList = "variant_id"), @Index(name = "idx_flash_sale_items_shop_id", columnList = "shop_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashSaleItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long campaignId;
    private Long variantId;
    private Long shopId;
    @Column(precision = 18, scale = 2)
    private BigDecimal salePrice;
    private Integer quota;
    @Builder.Default
    private Integer reserved = 0;
    @Builder.Default
    private Integer sold = 0;
    private Integer maxPerUser;
    @Version
    private Long version;
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
