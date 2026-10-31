package com.example.MyShop_API.controller;

import com.example.MyShop_API.constants.ApiPathConstants;

import com.example.MyShop_API.dto.request.CreateCouponRequest;
import com.example.MyShop_API.dto.request.UpdateCouponRequest;
import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.dto.response.CouponResponse;
import com.example.MyShop_API.mapper.CouponMapper;
import com.example.MyShop_API.service.coupon.ICouponService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(ApiPathConstants.BASE_COUPON)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CouponController {
    ICouponService couponService;
    CouponMapper couponMapper;

    @GetMapping(ApiPathConstants.COUPON_ALL)
    ResponseEntity<ApiResponse<List<CouponResponse>>> getCoupons() {
        return ResponseEntity.ok(new ApiResponse<>(200, "All coupon", couponMapper.toCouponResponses(couponService.getCoupons())));
    }

    @GetMapping(ApiPathConstants.COUPON_COUPON_AVAILABLE)
    ResponseEntity<ApiResponse<List<CouponResponse>>> getCouponsEnableFalse() {
        return ResponseEntity.ok(new ApiResponse<>(200, "All coupon", couponMapper.toCouponResponses(couponService.getAvailableCoupons())));
    }

    @GetMapping(ApiPathConstants.COUPON_GET_AVAILABLE_COUPONS_ORDER_TOTAL)
    ResponseEntity<ApiResponse<List<CouponResponse>>> getAvailableCoupons(@PathVariable BigDecimal orderTotal) {
        return ResponseEntity.ok(new ApiResponse<>(200, "available coupons", couponMapper.toCouponResponses(couponService.getAvailableCoupons(orderTotal))));
    }

    @PostMapping(ApiPathConstants.COUPON_COUPON_CREATE)
    ResponseEntity<ApiResponse<CouponResponse>> createCoupon(@Valid @RequestBody CreateCouponRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(200, "create coupon success", couponMapper.toCouponResponse(couponService.createCoupon(request))));
    }

    @PutMapping(ApiPathConstants.COUPON_COUPON_ID)
    ResponseEntity<ApiResponse<?>> updateCoupon(@Valid @RequestBody UpdateCouponRequest request, @PathVariable Long couponId) {
        couponService.updateCoupon(couponId, request);
        return ResponseEntity.ok(new ApiResponse<>(200, "update coupon success", null));
    }

    @DeleteMapping(ApiPathConstants.COUPON_COUPON_ID)
    ResponseEntity<ApiResponse<?>> deleteCoupon(@PathVariable Long couponId) {
        couponService.deleteCoupon(couponId);
        return ResponseEntity.ok(new ApiResponse<>(200, "delete coupon success", null));
    }
}
