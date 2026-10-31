package com.example.MyShop_API.repo;

import org.springframework.data.jpa.repository.Lock;

import com.example.MyShop_API.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCodeIgnoreCaseAndEnabledTrue(String code);

    @Query("""
                    SELECT c FROM Coupon c 
                    WHERE c.enabled = true 
                    AND (c.startsAt IS NULL OR  c.startsAt <= :now)
                    AND (c.endsAt IS NULL OR  c.endsAt >= :now)
                    AND (c.totalUsageLimit IS NULL OR c.redeemedCount <= c.totalUsageLimit)
                    AND (c.minimumOrderAmount IS NULL  OR c.minimumOrderAmount <= :orderTotal)
            """)
    List<Coupon> findAvailableCoupons(@Param("orderTotal") BigDecimal orderTotal,
                                      @Param("now") Instant now
    );


    List<Coupon> findCouponByEnabledTrue();

    boolean existsByCode(String code);
    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Coupon e WHERE e.id = :id")
    java.util.Optional<Coupon> findByIdForUpdate(@Param("id") Long id);
}
