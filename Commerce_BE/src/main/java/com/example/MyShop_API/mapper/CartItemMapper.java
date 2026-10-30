package com.example.MyShop_API.mapper;

import com.example.MyShop_API.entity.*;
import com.example.MyShop_API.dto.request.*;
import com.example.MyShop_API.dto.response.*;
import com.example.MyShop_API.service.model.ModelLookup;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class CartItemMapper {
    @Autowired protected ModelLookup models;

    public abstract CartItemResponse toResponse(CartItem item);

    @AfterMapping
    protected void fill(CartItem item, @MappingTarget CartItemResponse response) {
        var product = models.product(item.getProductId());
        response.setProductName(product == null ? null : product.getProductName());
    }
}
