package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.CouponProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponProductRepository extends JpaRepository<CouponProduct, Long> {
    java.util.List<CouponProduct> findByCouponId(Long id);
    void deleteByCouponId(Long id);
}
