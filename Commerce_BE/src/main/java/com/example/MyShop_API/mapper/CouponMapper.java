package com.example.MyShop_API.mapper;

import com.example.MyShop_API.entity.*;
import com.example.MyShop_API.dto.request.*;
import com.example.MyShop_API.dto.response.*;
import com.example.MyShop_API.service.model.ModelLookup;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class CouponMapper {
    @Autowired protected ModelLookup models;

    public abstract Coupon toCoupon(CreateCouponRequest request);
    public abstract CouponResponse toCouponResponse(Coupon coupon);
    public abstract List<CouponResponse> toCouponResponses(List<Coupon> coupons);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateCoupon(UpdateCouponRequest request, @MappingTarget Coupon coupon);

    @AfterMapping
    protected void fill(Coupon coupon, @MappingTarget CouponResponse response) {
        response.setCategoryIds(models.couponCategories(coupon.getCouponId()).stream().map(Category::getCategoryId).toList());
        response.setProductIds(models.couponProducts(coupon.getCouponId()).stream().map(Product::getProductId).toList());
    }
}
