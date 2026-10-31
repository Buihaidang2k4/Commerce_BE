package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "coupons", indexes = {@Index(name = "idx_coupons_shop_id", columnList = "shop_id"), @Index(name = "idx_coupons_code", columnList = "code")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long shopId;
    private String code;
    private String scope;
    private String discountType;
    @Column(precision = 18, scale = 2)
    private BigDecimal discountPercentage;
    @Column(precision = 18, scale = 2)
    private BigDecimal discountAmount;
    @Column(precision = 18, scale = 2)
    private BigDecimal maximumDiscountAmount;
    @Column(precision = 18, scale = 2)
    private BigDecimal minimumOrderAmount;
    private Instant startsAt;
    private Instant endsAt;
    private boolean enabled;
    private Integer totalUsageLimit;
    private Integer redeemedCount;
    private boolean limitPerUser;
    private Integer perUserUsageLimit;

    public boolean isUsable() { Instant now = Instant.now(); return enabled && (startsAt == null || !now.isBefore(startsAt)) && (endsAt == null || now.isBefore(endsAt)) && (totalUsageLimit == null || (redeemedCount == null ? 0 : redeemedCount) < totalUsageLimit); }
    public void incrementUsedCount() { redeemedCount = (redeemedCount == null ? 0 : redeemedCount) + 1; }
}
