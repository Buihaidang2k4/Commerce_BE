package com.example.MyShop_API.mapper;

import com.example.MyShop_API.repo.CartItemRepository;
import java.util.Optional;

import com.example.MyShop_API.entity.*;
import com.example.MyShop_API.dto.request.*;
import com.example.MyShop_API.dto.response.*;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ShoppingCartMapper {
    @Autowired protected CartItemRepository cartItemRepository;
    @Autowired protected CartItemMapper items;

    public abstract ShoppingCart toEntity(CartRequest request);

    @Mapping(source = "id", target = "cartId")
    @Mapping(source = "subtotalAmount", target = "totalPrice")
    public abstract CartResponse toResponse(ShoppingCart cart);
    public abstract List<CartResponse> toResponseList(List<ShoppingCart> carts);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

    @Mapping(target = "id", ignore = true)
    public abstract void update(CartRequest request, @MappingTarget ShoppingCart cart);

    @AfterMapping
    protected void fill(ShoppingCart cart, @MappingTarget CartResponse response) {
        response.setItems(Optional.ofNullable(cart.getId()).map(cartItemRepository::findByCartId).orElseGet(List::of).stream().map(items::toResponse).toList());
    }
}
