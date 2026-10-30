package com.example.MyShop_API.mapper;

import com.example.MyShop_API.entity.*;
import com.example.MyShop_API.dto.request.*;
import com.example.MyShop_API.dto.response.*;
import com.example.MyShop_API.service.model.ModelLookup;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class CartMapper {
    @Autowired protected ModelLookup models;
    @Autowired protected CartItemMapper items;
    public abstract Cart toEntity(CartRequest request);
    public abstract CartResponse toResponse(Cart cart);
    public abstract List<CartResponse> toResponseList(List<Cart> carts);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void update(CartRequest request, @MappingTarget Cart cart);

    @AfterMapping
    protected void fill(Cart cart, @MappingTarget CartResponse response) {
        response.setItems(models.cartItems(cart.getCartId()).stream().map(items::toResponse).toList());
    }
}
