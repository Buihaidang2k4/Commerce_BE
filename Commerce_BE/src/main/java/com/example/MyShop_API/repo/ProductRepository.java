package com.example.MyShop_API.repo;

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
    Optional<Product> getProductByProductName(String productName);

    @org.springframework.data.jpa.repository.Query("SELECT p FROM Product p WHERE p.categoryId IN (SELECT c.categoryId FROM Category c WHERE c.categoryName = :categoryName)")
    Page<Product> findByCategory_CategoryName(@org.springframework.data.repository.query.Param("categoryName") String categoryName, Pageable pageable);

    Optional<Product> findBySlug(String slug);

    boolean existsProductByProductName(String productName);
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("SELECT e FROM Product e WHERE e.productId = :id")
    java.util.Optional<Product> findByIdForUpdate(@org.springframework.data.repository.query.Param("id") Long id);
}
