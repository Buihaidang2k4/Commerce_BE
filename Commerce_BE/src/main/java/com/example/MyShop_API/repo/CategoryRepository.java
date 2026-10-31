package com.example.MyShop_API.repo;

import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.MyShop_API.entity.Category;
import com.example.MyShop_API.mapper.CategoryMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String categoryName);

    Category findByName(String categoryName);

    @Query("SELECT c FROM Category c WHERE c.id IN (SELECT cc.categoryId FROM CouponCategory cc WHERE cc.couponId = :couponId)")
    Set<Category> findAllByCouponId(@Param("couponId") Long couponId);
}
