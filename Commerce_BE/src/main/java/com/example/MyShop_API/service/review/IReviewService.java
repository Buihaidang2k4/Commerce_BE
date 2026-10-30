package com.example.MyShop_API.service.review;

import com.example.MyShop_API.dto.request.CreateReviewRequest;
import com.example.MyShop_API.dto.response.ReviewResponse;
import com.example.MyShop_API.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IReviewService {
    Review createReview(CreateReviewRequest request, Long profileId, Long orderId, Long productId);

    Page<ReviewResponse> findByProductId(Long productId, Pageable pageable);

}
