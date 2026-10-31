package com.example.MyShop_API.mapper;

import com.example.MyShop_API.repo.CouponRepository;
import com.example.MyShop_API.repo.OrderItemRepository;
import com.example.MyShop_API.repo.OrderShippingAddressRepository;
import java.util.Optional;

import com.example.MyShop_API.entity.*;
import com.example.MyShop_API.dto.request.*;
import com.example.MyShop_API.dto.response.*;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class OrderMapper {
    @Autowired protected OrderShippingAddressRepository orderShippingAddressRepository;
    @Autowired protected CouponRepository couponRepository;
    @Autowired protected OrderItemRepository orderItemRepository;
    @Autowired protected OrderItemMapper items;
    @Autowired protected CouponMapper coupons;

    @Mapping(source = "profileId", target = "userProfileId")
    @Mapping(source = "shippingFee", target = "shippingAmount")
    public abstract Order toEntity(OrderRequest request);



    @Mapping(source = "id", target = "orderId")
    @Mapping(source = "userProfileId", target = "profileId")
    @Mapping(source = "shippingAmount", target = "shippingFee")
    @Mapping(source = "status", target = "orderStatus")
    public abstract OrderResponse toResponse(Order order);
    public abstract List<OrderResponse> toResponse(List<Order> orders);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)


    @Mapping(target = "id", ignore = true)
    @Mapping(source = "profileId", target = "userProfileId")
    @Mapping(source = "shippingFee", target = "shippingAmount")
    public abstract void update(OrderRequest request, @MappingTarget Order order);

    @AfterMapping
    protected void fill(Order order, @MappingTarget OrderResponse response) {
        response.setOrderItemResponses(Optional.ofNullable(order.getId()).map(orderItemRepository::findByOrderId).orElseGet(List::of).stream().map(items::toResponse).toList());
        response.setCouponResponse(coupons.toCouponResponse(Optional.ofNullable(order.getCouponId()).flatMap(couponRepository::findById).orElse(null)));
        var address = Optional.ofNullable(order.getId()).map(orderShippingAddressRepository::findOrderDeliveryAddressByOrderId).orElse(null);
        response.setDeliveryAddressId(address == null ? null : address.getId());
    }
}
