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






    @Mapping(source = "discountPercent", target = "discountPercentage")
    @Mapping(source = "maxDiscountAmount", target = "maximumDiscountAmount")
    @Mapping(source = "minOrderValue", target = "minimumOrderAmount")
    @Mapping(source = "startDate", target = "startsAt")
    @Mapping(source = "expiryDate", target = "endsAt")
    @Mapping(source = "usageLimit", target = "totalUsageLimit")
    @Mapping(source = "maxUsesPerUser", target = "perUserUsageLimit")
    public abstract Coupon toCoupon(CreateCouponRequest request);





    @Mapping(source = "id", target = "couponId")
    @Mapping(source = "discountPercentage", target = "discountPercent")
    @Mapping(source = "maximumDiscountAmount", target = "maxDiscountAmount")
    @Mapping(source = "minimumOrderAmount", target = "minOrderValue")
    @Mapping(source = "startsAt", target = "startDate")
    @Mapping(source = "endsAt", target = "expiryDate")
    public abstract CouponResponse toCouponResponse(Coupon coupon);
    public abstract List<CouponResponse> toCouponResponses(List<Coupon> coupons);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)




    @Mapping(target = "id", ignore = true)
    @Mapping(source = "maxDiscountAmount", target = "maximumDiscountAmount")
    @Mapping(source = "minOrderValue", target = "minimumOrderAmount")
    @Mapping(source = "startDate", target = "startsAt")
    @Mapping(source = "expiryDate", target = "endsAt")
    public abstract void updateCoupon(UpdateCouponRequest request, @MappingTarget Coupon coupon);

    @AfterMapping
    protected void fill(Coupon coupon, @MappingTarget CouponResponse response) {
        response.setCategoryIds(models.couponCategories(coupon.getId()).stream().map(Category::getId).toList());
        response.setProductIds(models.couponProducts(coupon.getId()).stream().map(Product::getId).toList());
    }
}
