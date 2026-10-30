package com.example.MyShop_API.mapper;

import com.example.MyShop_API.Enum.CouponScope;
import com.example.MyShop_API.dto.request.CreateCouponRequest;
import com.example.MyShop_API.dto.request.UpdateCouponRequest;
import com.example.MyShop_API.dto.response.CouponResponse;
import com.example.MyShop_API.entity.Category;
import com.example.MyShop_API.entity.Coupon;
import com.example.MyShop_API.entity.Product;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CouponMapper {
    Coupon toCoupon(CreateCouponRequest request);

    CouponResponse toCouponResponse(Coupon coupon);

    default List<CouponResponse> toCouponResponses(List<Coupon> coupons) {
        if (coupons == null || coupons.isEmpty()) {
            return List.of();
        }

        return coupons.stream().map(c -> {
            CouponResponse res = new CouponResponse();

            res.setCouponId(c.getCouponId());
            res.setCode(c.getCode());
            res.setScope(c.getScope());
            res.setDiscountType(c.getDiscountType());
            res.setDiscountPercent(c.getDiscountPercent());
            res.setDiscountAmount(c.getDiscountAmount());
            res.setMaxDiscountAmount(c.getMaxDiscountAmount());
            res.setMinOrderValue(c.getMinOrderValue());
            res.setStartDate(c.getStartDate());
            res.setExpiryDate(c.getExpiryDate());
            res.setEnabled(c.isEnabled());

            res.setCategoryIds(
                    c.getCategories() == null
                            ? List.of()
                            : c.getCategories()
                            .stream()
                            .map(Category::getCategoryId)
                            .toList()
            );

            res.setProductIds(
                    c.getProducts() == null
                            ? List.of()
                            : c.getProducts()
                            .stream()
                            .map(Product::getProductId)
                            .toList()
            );

            return res;
        }).toList();
    }


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCoupon(UpdateCouponRequest request, @MappingTarget Coupon coupon);
}
