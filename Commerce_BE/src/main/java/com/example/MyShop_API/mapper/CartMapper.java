package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.request.CartRequest;
import com.example.MyShop_API.dto.response.CartResponse;
import com.example.MyShop_API.entity.Cart;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CartItemMapper.class})
public interface CartMapper {
    Cart toEntity(CartRequest cartRequest);

    @Mapping(source = "cartItems", target = "items")
    CartResponse toResponse(Cart cart);

    List<CartResponse> toResponseList(List<Cart> cartList);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(CartRequest cartRequest, @MappingTarget Cart cart);
}
