package com.example.MyShop_API.mapper;

import com.example.MyShop_API.entity.*;
import com.example.MyShop_API.dto.request.*;
import com.example.MyShop_API.dto.response.*;
import com.example.MyShop_API.service.model.ModelLookup;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ProductMapper {
    @Autowired protected ModelLookup models;
    @Autowired protected InventoryMapper inventories;
    @Autowired protected ImageMapper images;
    public abstract Product toEntity(AddProductRequest request);
    public abstract ProductResponse toResponse(Product product);
    public abstract List<ProductResponse> toResponseList(List<Product> products);
    @Mapping(target = "productId", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void update(AddProductRequest request, @MappingTarget Product product);

    @AfterMapping
    protected void fill(Product product, @MappingTarget ProductResponse response) {
        response.setCategory(models.category(product.getCategoryId()));
        response.setInventory(inventories.toInventoryDTO(models.inventory(product.getProductId())));
        response.setImages(models.images(product.getProductId()).stream().map(images::toImageDTO).toList());
    }
}
