package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "coupons", indexes = {@Index(name = "idx_coupons_shop_id", columnList = "shop_id"), @Index(name = "idx_coupons_code", columnList = "code")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;
    private Long shopId;
    private String code;
    @Builder.Default
    private String scope = "GLOBAL";
    private String discountType;
    @Column(precision = 18, scale = 2)
    private BigDecimal discountPercent;
    @Column(precision = 18, scale = 2)
    private BigDecimal discountAmount;
    @Column(precision = 18, scale = 2)
    private BigDecimal maxDiscountAmount;
    @Column(precision = 18, scale = 2)
    private BigDecimal minOrderValue;
    private LocalDateTime startDate;
    private LocalDateTime expiryDate;
    @Builder.Default
    private boolean enabled = true;
    private Integer usageLimit;
    @Builder.Default
    private Integer usedCount = 0;
    @Builder.Default
    private boolean limitPerUser = true;
    @Builder.Default
    private Integer maxUsesPerUser = 1;
    public boolean isUsable() {
        LocalDateTime now = LocalDateTime.now();
        return enabled && (startDate == null || !now.isBefore(startDate)) && (expiryDate == null || !now.isAfter(expiryDate)) && (usageLimit == null || usedCount < usageLimit);
    }
    public void incrementUsedCount() { usedCount = (usedCount == null ? 0 : usedCount) + 1; }
}
