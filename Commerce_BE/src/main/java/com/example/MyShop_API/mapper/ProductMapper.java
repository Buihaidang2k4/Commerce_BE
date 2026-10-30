package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.request.AddProductRequest;
import com.example.MyShop_API.dto.response.ProductResponse;
import com.example.MyShop_API.entity.Product;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class, InventoryMapper.class})
public interface ProductMapper {

    @Mapping(target = "category.categoryId", source = "categoryId")
    Product toEntity(AddProductRequest addProductRequest);

    ProductResponse toResponse(Product product);

    List<ProductResponse> toResponseList(List<Product> products);

    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "category", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(AddProductRequest addProductRequest, @MappingTarget Product product);
}
