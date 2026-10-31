package com.example.MyShop_API.mapper;

import com.example.MyShop_API.repo.ProductRepository;
import java.util.Optional;

import com.example.MyShop_API.entity.*;
import com.example.MyShop_API.dto.request.*;
import com.example.MyShop_API.dto.response.*;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class CartItemMapper {
    @Autowired protected ProductRepository productRepository;

    @Mapping(source = "id", target = "cartItemId")
    @Mapping(source = "lineAmount", target = "totalPrice")
    public abstract CartItemResponse toResponse(CartItem item);

    @AfterMapping
    protected void fill(CartItem item, @MappingTarget CartItemResponse response) {
        var product = Optional.ofNullable(item.getProductId()).flatMap(productRepository::findById).orElse(null);
        response.setProductName(product == null ? null : product.getName());
    }
}
