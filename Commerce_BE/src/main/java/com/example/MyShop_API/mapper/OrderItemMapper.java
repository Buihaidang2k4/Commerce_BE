package com.example.MyShop_API.mapper;

import com.example.MyShop_API.entity.*;
import com.example.MyShop_API.dto.request.*;
import com.example.MyShop_API.dto.response.*;
import com.example.MyShop_API.service.model.ModelLookup;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class OrderItemMapper {
    @Autowired protected ModelLookup models;

    public abstract OrderItem toEntity(OrderItemRequest request);
    public abstract OrderItemResponse toResponse(OrderItem item);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateOrder(OrderItemRequest request, @MappingTarget OrderItem item);

    @AfterMapping
    protected void fill(OrderItem item, @MappingTarget OrderItemResponse response) {
        var product = models.product(item.getProductId());
        response.setProductName(item.getProductTitle() != null ? item.getProductTitle() : product == null ? null : product.getProductName());
    }
}
