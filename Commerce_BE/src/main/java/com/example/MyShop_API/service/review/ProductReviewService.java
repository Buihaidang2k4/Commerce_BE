package com.example.MyShop_API.service.review;

import com.example.MyShop_API.constants.OrderStatusConstants;
import com.example.MyShop_API.dto.request.CreateReviewRequest;
import com.example.MyShop_API.dto.response.ReviewResponse;
import com.example.MyShop_API.entity.Order;
import com.example.MyShop_API.entity.Product;
import com.example.MyShop_API.entity.ProductReview;
import com.example.MyShop_API.entity.UserProfile;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.constants.CommonMessageConstants;
import com.example.MyShop_API.mapper.ProductReviewMapper;
import com.example.MyShop_API.repo.OrderRepository;
import com.example.MyShop_API.repo.ProductRepository;
import com.example.MyShop_API.repo.ProductReviewRepository;
import com.example.MyShop_API.repo.UserProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductReviewService implements IProductReviewService {
    ProductReviewRepository reviewRepository;
    OrderRepository orderRepository;
    ProductRepository productRepository;
    UserProfileRepository profileRepository;
    ProductReviewMapper reviewMapper;

    @Override
    public ProductReview createReview(CreateReviewRequest request, Long profileId, Long orderId, Long productId) {
        log.info("====================== START CREATE REVIEW =======================");

        Product product = productRepository.findById(productId).orElseThrow(() -> new AppException(CommonMessageConstants.PRODUCT_NOT_EXISTED));
        UserProfile profile = profileRepository.findById(profileId).orElseThrow(() -> new AppException(CommonMessageConstants.PROFILE_NOT_EXISTED));
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(CommonMessageConstants.ORDER_NOT_EXISTED));

        boolean purchased = orderRepository.hasPurchasedProductInOrder(profile.getId(), product.getId(), order.getId(), List.of(OrderStatusConstants.DELIVERED));

        if (!purchased) throw new AppException(CommonMessageConstants.REVIEW_NOT_PURCHASED);

        // 1. Kiểm tra đơn hàng có thuộc về user không
        if (!order.getUserProfileId().equals(profileId)) {
            throw new AppException(CommonMessageConstants.ORDER_NOT_BELONG_TO_USER);
        }

        // 2. Kiểm tra đơn đã giao thành công chưa
        if (!OrderStatusConstants.DELIVERED.equals(order.getStatus())) {
            throw new AppException(CommonMessageConstants.ORDER_NOT_DELIVERED);
        }

        // Chỉ cho đánh giá 1 lần cho 1 ĐƠN HÀNG + 1 SẢN PHẨM
        boolean alreadyReviewed = reviewRepository.existsReviewForOrderAndProduct(profileId, orderId, productId);

        if (alreadyReviewed) throw new AppException(CommonMessageConstants.REVIEW_ALREADY_EXISTS_FOR_THIS_ORDER);

        String usernameRating = profile.getDisplayName() != null ? profile.getDisplayName() : "anonymous" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        ProductReview review = ProductReview.builder().productId(product.getId()).userProfileId(profile.getId()).orderId(order.getId()).reviewerNameSnapshot(usernameRating).rating(request.getRating()).content(request.getComment()).createdAt(Instant.now()).build();


        //  reviewCount & avgRating incremental
        calculateRatingStatsOrReviewCount(product, request.getRating());

        log.info("====================== END CREATE REVIEW =======================");
        return reviewRepository.save(review);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> findByProductId(Long productId, Pageable pageable) {
        return reviewRepository.findByProductIdAndDeletedFalse(productId, pageable).map(reviewMapper::toResponse);
    }

    private void calculateRatingStatsOrReviewCount(Product product, int rating) {
        int newReviewCount = product.getReviewCount() + 1;
        double newAvgRating = (product.getAverageRating() * product.getReviewCount() + rating) / newReviewCount;

        product.setReviewCount(newReviewCount);
        product.setAverageRating(newAvgRating);

        productRepository.save(product);
    }
}
