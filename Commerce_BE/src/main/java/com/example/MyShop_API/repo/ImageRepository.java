package com.example.MyShop_API.repo;

import com.example.MyShop_API.dto.request.ImageDTO;
import com.example.MyShop_API.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    @Query("SELECT i FROM Image i WHERE i.product.productId =:productId ")
    List<Image> findImageByProductId(@Param("productId") Long productId);

}
