package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "flash_sale_campaigns")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashSaleCampaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Instant startsAt;
    private Instant endsAt;
    @Builder.Default
    private String status = "DRAFT";
    @Builder.Default
    private Integer paymentTtlSeconds = 600;
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
