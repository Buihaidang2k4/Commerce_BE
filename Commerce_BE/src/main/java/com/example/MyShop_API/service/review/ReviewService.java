package com.example.MyShop_API.service.review;

import com.example.MyShop_API.constants.OrderStatus;
import com.example.MyShop_API.dto.request.CreateReviewRequest;
import com.example.MyShop_API.dto.response.ReviewResponse;
import com.example.MyShop_API.entity.Order;
import com.example.MyShop_API.entity.Product;
import com.example.MyShop_API.entity.Review;
import com.example.MyShop_API.entity.UserProfile;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.constants.CommonMessageUtils;
import com.example.MyShop_API.mapper.ReviewMapper;
import com.example.MyShop_API.repo.OrderRepository;
import com.example.MyShop_API.repo.ProductRepository;
import com.example.MyShop_API.repo.ReviewRepository;
import com.example.MyShop_API.repo.UserProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ReviewService implements IReviewService {
    ReviewRepository reviewRepository;
    OrderRepository orderRepository;
    ProductRepository productRepository;
    UserProfileRepository profileRepository;
    ReviewMapper reviewMapper;

    @Override
    public Review createReview(CreateReviewRequest request, Long profileId, Long orderId, Long productId) {
        log.info("====================== START CREATE REVIEW =======================");

        Product product = productRepository.findById(productId).orElseThrow(() -> new AppException(CommonMessageUtils.PRODUCT_NOT_EXISTED));
        UserProfile profile = profileRepository.findById(profileId).orElseThrow(() -> new AppException(CommonMessageUtils.PROFILE_NOT_EXISTED));
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(CommonMessageUtils.ORDER_NOT_EXISTED));

        boolean purchased = orderRepository.hasPurchasedProductInOrder(profile.getProfileId(), product.getProductId(), order.getOrderId(), List.of(OrderStatus.DELIVERED));

        if (!purchased) throw new AppException(CommonMessageUtils.REVIEW_NOT_PURCHASED);

        // 1. Kiểm tra đơn hàng có thuộc về user không
        if (!order.getProfileId().equals(profileId)) {
            throw new AppException(CommonMessageUtils.ORDER_NOT_BELONG_TO_USER);
        }

        // 2. Kiểm tra đơn đã giao thành công chưa
        if (!OrderStatus.DELIVERED.equals(order.getOrderStatus())) {
            throw new AppException(CommonMessageUtils.ORDER_NOT_DELIVERED);
        }

        // Chỉ cho đánh giá 1 lần cho 1 ĐƠN HÀNG + 1 SẢN PHẨM
        boolean alreadyReviewed = reviewRepository.existsReviewForOrderAndProduct(profileId, orderId, productId);

        if (alreadyReviewed) throw new AppException(CommonMessageUtils.REVIEW_ALREADY_EXISTS_FOR_THIS_ORDER);

        String usernameRating = profile.getUsername() != null ? profile.getUsername() : "anonymous" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        Review review = Review.builder().productId(product.getProductId()).profileId(profile.getProfileId()).orderId(order.getOrderId()).customerName(usernameRating).rating(request.getRating()).comment(request.getComment()).createdAt(LocalDateTime.now()).build();


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
        double newAvgRating = (product.getAvgRating() * product.getReviewCount() + rating) / newReviewCount;

        product.setReviewCount(newReviewCount);
        product.setAvgRating(newAvgRating);

        productRepository.save(product);
    }
}
