package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.CouponCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponCategoryRepository extends JpaRepository<CouponCategory, Long> {
    java.util.List<CouponCategory> findByCouponId(Long id);
    void deleteByCouponId(Long id);
}
