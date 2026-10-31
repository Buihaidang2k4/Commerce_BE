package com.example.MyShop_API.repo;

import java.util.Set;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.MyShop_API.entity.Category;
import com.example.MyShop_API.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Optional<Product> getProductByName(String productName);

    @Query("SELECT p FROM Product p WHERE p.categoryId IN (SELECT c.id FROM Category c WHERE c.name = :categoryName)")
    Page<Product> findByCategoryName(@Param("categoryName") String categoryName, Pageable pageable);

    Optional<Product> findBySlug(String slug);

    boolean existsProductByName(String productName);
    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Product e WHERE e.id = :id")
    java.util.Optional<Product> findByIdForUpdate(@Param("id") Long id);
    @Query("SELECT p FROM Product p WHERE p.id IN (SELECT cp.productId FROM CouponProduct cp WHERE cp.couponId = :couponId)")
    Set<Product> findAllByCouponId(@Param("couponId") Long couponId);
}
