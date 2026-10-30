package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.request.OrderRequest;
import com.example.MyShop_API.dto.response.OrderResponse;
import com.example.MyShop_API.entity.Order;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {

    Order toEntity(OrderRequest orderRequest);

    @Mapping(source = "payment.paymentId", target = "paymentId")
    @Mapping(source = "deliveryAddress.id", target = "deliveryAddressId")
    @Mapping(source = "orderItems", target = "orderItemResponses")
    @Mapping(source = "profile.profileId", target = "profileId")
    @Mapping(source = "coupon", target = "couponResponse")
    OrderResponse toResponse(Order order);

    List<OrderResponse> toResponse(List<Order> orders);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(OrderRequest orderRequest, @MappingTarget Order order);
}
