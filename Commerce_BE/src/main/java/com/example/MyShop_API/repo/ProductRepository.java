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

    Page<Product> findByCategory_CategoryName(String categoryName, Pageable pageable);

    Optional<Product> findBySlug(String slug);

    boolean existsProductByProductName(String productName);
}
