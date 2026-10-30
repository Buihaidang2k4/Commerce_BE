package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("""
            SELECT COUNT(r) > 0 
            FROM Review r 
            WHERE r.profile.profileId = :profileId 
              AND r.order.orderId = :orderId 
              AND r.product.productId = :productId 
              AND r.deleted = false
            """)
    boolean existsReviewForOrderAndProduct(
            @Param("profileId") Long profileId,
            @Param("productId") Long productId,
            @Param("orderId") Long orderId);

    Page<Review> findByProductProductIdAndDeletedFalse(Long productId, Pageable pageable);

    @Query("""
            SELECT 
            COALESCE(AVG(r.rating),0) ,
            COUNT(r)
            FROM Review r WHERE r.product.productId =:productId AND r.deleted = false 
            """)
    Object findRatingStats(@Param("productId") Long productId);

}
