package com.example.MyShop_API.controller;

import com.example.MyShop_API.constants.ApiPath;

import com.example.MyShop_API.dto.request.CreateReviewRequest;
import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.dto.response.ReviewResponse;
import com.example.MyShop_API.entity.ProductReview;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.mapper.ProductReviewMapper;
import com.example.MyShop_API.service.review.IProductReviewService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestController
@RequestMapping(ApiPath.BASE_REVIEW)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductReviewController {
    IProductReviewService reviewService;
    ProductReviewMapper reviewMapper;

    @GetMapping(ApiPath.REVIEW_PRODUCT_PRODUCT_ID)
    public ResponseEntity<ApiResponse<?>> getReviewsByProduct(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        try {
            Sort sort = direction.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<ReviewResponse> reviewRes = reviewService.findByProductId(productId, pageable);

            Map<String, Object> res = new HashMap<>();
            res.put("content", reviewRes.getContent());
            res.put("size", reviewRes.getNumberOfElements());
            res.put("direction", direction);
            res.put("currentPage", reviewRes.getNumber());
            res.put("totalItems", reviewRes.getTotalElements());
            res.put("totalPages", reviewRes.getTotalPages());
            res.put("sortBy", sortBy);

            return ResponseEntity.ok(
                    new ApiResponse<>(200, "Lấy đánh giá sản phẩm thành công!", res)
            );
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            if (e instanceof AppException appException) throw appException;
            throw new IllegalStateException(e);
        }
    }

    @PostMapping(ApiPath.REVIEW_CREATE_REVIEW)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewResponse> createReview(
            @Valid @RequestBody CreateReviewRequest request,
            @RequestParam("profileId") Long profileId,
            @RequestParam("orderId") Long orderId,
            @RequestParam("productId") Long productId
    ) {
        ProductReview review = reviewService.createReview(request, profileId, orderId, productId);
        ReviewResponse response = reviewMapper.toResponse(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}
