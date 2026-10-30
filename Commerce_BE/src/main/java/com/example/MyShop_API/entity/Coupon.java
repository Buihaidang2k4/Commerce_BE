package com.example.MyShop_API.entity;

import com.example.MyShop_API.Enum.CouponScope;
import com.example.MyShop_API.Enum.DiscountType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "coupons", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long couponId;

    @Column(unique = true, length = 50, nullable = false)
    String code;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    CouponScope scope = CouponScope.GLOBAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    DiscountType discountType;
    BigDecimal discountPercent; // 10.00 = 10%
    BigDecimal discountAmount; // 5000VNĐ
    BigDecimal maxDiscountAmount; // maximum discount limit
    BigDecimal minOrderValue; // Minimum conditions for discount
    LocalDateTime startDate;
    LocalDateTime expiryDate;
    boolean enabled = true;
    Integer usageLimit;
    Integer usedCount;
    boolean limitPerUser = true;
    @Column(name = "max_uses_per_user")
    Integer maxUsesPerUser = 1;

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "coupon_categories",
            joinColumns = @JoinColumn(name = "coupon_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    Set<Category> categories = new HashSet<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "coupon_products",
            joinColumns = @JoinColumn(name = "coupon_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    Set<Product> products = new HashSet<>();


    public boolean isUsable() {
        LocalDateTime now = LocalDateTime.now();
        if (!enabled) return false;
        if (startDate != null && now.isBefore(startDate)) return false;
        if (expiryDate != null && now.isAfter(expiryDate)) return false;
        if (usageLimit != null && usedCount >= usageLimit) return false;
        return true;
    }

    public void incrementUsedCount() {
        this.usedCount++;
    }
}
