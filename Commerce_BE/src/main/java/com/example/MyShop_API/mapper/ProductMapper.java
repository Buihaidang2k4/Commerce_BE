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
    @Autowired protected StockBalanceMapper inventories;
    @Autowired protected ProductImageMapper images;



    @Mapping(source = "productName", target = "name")
    @Mapping(source = "bio", target = "shortDescription")
    @Mapping(source = "soldCount", target = "soldQuantity")
    @Mapping(source = "avgRating", target = "averageRating")
    public abstract Product toEntity(AddProductRequest request);




    @Mapping(source = "id", target = "productId")
    @Mapping(source = "name", target = "productName")
    @Mapping(source = "shortDescription", target = "bio")
    @Mapping(source = "soldQuantity", target = "soldCount")
    @Mapping(source = "averageRating", target = "avgRating")
    public abstract ProductResponse toResponse(Product product);
    public abstract List<ProductResponse> toResponseList(List<Product> products);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)




    @Mapping(target = "id", ignore = true)
    @Mapping(source = "productName", target = "name")
    @Mapping(source = "bio", target = "shortDescription")
    @Mapping(target = "soldQuantity", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "reviewCount", ignore = true)
    public abstract void update(AddProductRequest request, @MappingTarget Product product);

    @AfterMapping
    protected void fill(Product product, @MappingTarget ProductResponse response) {
        response.setCreateAt(product.getCreatedAt() == null ? null : product.getCreatedAt().atZone(java.time.ZoneOffset.UTC).toLocalDate());
        response.setUpdateAt(product.getUpdatedAt() == null ? null : product.getUpdatedAt().atZone(java.time.ZoneOffset.UTC).toLocalDate());
        response.setCategory(models.category(product.getCategoryId()));
        response.setInventory(inventories.toInventoryDTO(models.inventory(product.getId())));
        response.setImages(models.images(product.getId()).stream().map(images::toImageDTO).toList());
    }
}
