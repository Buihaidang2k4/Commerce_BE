package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCodeIgnoreCaseAndEnabledTrue(String code);

    @Query("""
                    SELECT c FROM Coupon c 
                    WHERE c.enabled = true 
                    AND (c.startDate IS NULL OR  c.startDate <= :now)
                    AND (c.expiryDate IS NULL OR  c.expiryDate >= :now)
                    AND (c.usageLimit IS NULL OR c.usedCount <= c.usageLimit)
                    AND (c.minOrderValue IS NULL  OR c.minOrderValue <= :orderTotal)
            """)
    List<Coupon> findAvailableCoupons(@Param("orderTotal") BigDecimal orderTotal,
                                      @Param("now") LocalDateTime now
    );


    List<Coupon> findCouponByEnabledTrue();

    boolean existsByCode(String code);
}
