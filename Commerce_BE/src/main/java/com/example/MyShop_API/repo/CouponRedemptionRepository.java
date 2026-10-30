package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.CouponRedemption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRedemptionRepository extends JpaRepository<CouponRedemption, Long> {
}
