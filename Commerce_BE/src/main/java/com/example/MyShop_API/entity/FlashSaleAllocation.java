package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "flash_sale_allocations", indexes = {@Index(name = "idx_flash_sale_allocations_flash_sale_campaign_id", columnList = "flash_sale_campaign_id"), @Index(name = "idx_flash_sale_allocations_flash_sale_offer_id", columnList = "flash_sale_offer_id"), @Index(name = "idx_flash_sale_allocations_user_id", columnList = "user_id"), @Index(name = "idx_flash_sale_allocations_order_id", columnList = "order_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FlashSaleAllocation extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long flashSaleCampaignId;
    private Long flashSaleOfferId;
    private Long userId;
    private Long orderId;
    private Integer allocatedQuantity;
    private String status;
    private Instant expiresAt;


}
