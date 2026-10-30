package com.example.MyShop_API.service.coupon;

import com.example.MyShop_API.dto.request.CreateCouponRequest;
import com.example.MyShop_API.dto.request.UpdateCouponRequest;
import com.example.MyShop_API.dto.response.CategoryResponse;
import com.example.MyShop_API.dto.response.ProductResponse;
import com.example.MyShop_API.entity.Coupon;
import com.example.MyShop_API.entity.Order;
import com.example.MyShop_API.entity.UserProfile;

import java.math.BigDecimal;
import java.util.List;

public interface ICouponService {
    List<Coupon> getCoupons();


    List<Coupon> getAvailableCoupons(BigDecimal orderTotal);

    List<Coupon> getAvailableCoupons();

    Coupon createCoupon(CreateCouponRequest request);

    BigDecimal applyCouponToOrder(String couponCode, BigDecimal orderTotal, Order order, UserProfile profile);

    void updateCoupon(Long couponId, UpdateCouponRequest request);

    void deleteCoupon(Long couponId);
}
