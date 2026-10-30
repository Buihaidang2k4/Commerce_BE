package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.response.CartItemResponse;
import com.example.MyShop_API.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    @Mapping(source = "product.productId", target = "productId")
    @Mapping(source = "product.productName", target = "productName")
    CartItemResponse toResponse(CartItem cartItem);
}
