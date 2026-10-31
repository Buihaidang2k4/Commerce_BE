package com.example.MyShop_API.repo;

import com.example.MyShop_API.dto.request.ImageDTO;
import com.example.MyShop_API.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    @Query("SELECT i FROM ProductImage i WHERE i.productId =:productId ")
    List<ProductImage> findImageByProductId(@Param("productId") Long productId);

    void deleteByProductId(Long productId);
}
