package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.request.OrderItemRequest;
import com.example.MyShop_API.dto.response.OrderItemResponse;
import com.example.MyShop_API.entity.OrderItem;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    OrderItem toEntity(OrderItemRequest orderItemRequest);


    @Mapping(source = "product.productId", target = "productId")
    @Mapping(source = "product.productName", target = "productName")
    OrderItemResponse toResponse(OrderItem orderItem);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateOrder(OrderItemRequest orderItemRequest, @MappingTarget OrderItem orderItem);
}
