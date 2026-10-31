package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "flash_sale_offers", indexes = {@Index(name = "idx_flash_sale_offers_flash_sale_campaign_id", columnList = "flash_sale_campaign_id"), @Index(name = "idx_flash_sale_offers_variant_id", columnList = "variant_id"), @Index(name = "idx_flash_sale_offers_shop_id", columnList = "shop_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FlashSaleOffer extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long flashSaleCampaignId;
    private Long variantId;
    private Long shopId;
    @Column(precision = 18, scale = 2)
    private BigDecimal salePrice;
    private Integer quotaQuantity;
    private Integer reservedQuantity;
    private Integer soldQuantity;
    private Integer perUserQuantityLimit;
    @Version
    private Long version;


}
