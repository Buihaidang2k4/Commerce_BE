package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.request.CategoryRequest;
import com.example.MyShop_API.dto.response.CategoryResponse;
import com.example.MyShop_API.entity.Category;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(source = "categoryName", target = "name")
    Category toEntity(CategoryRequest categoryRequest);

    @Mapping(source = "id", target = "categoryId")
    @Mapping(source = "name", target = "categoryName")
    @Mapping(target = "createAt", expression = "java(category.getCreatedAt() == null ? null : category.getCreatedAt().atZone(java.time.ZoneOffset.UTC).toLocalDate())")
    @Mapping(target = "updateAt", expression = "java(category.getUpdatedAt() == null ? null : category.getUpdatedAt().atZone(java.time.ZoneOffset.UTC).toLocalDate())")
    CategoryResponse toResponse(Category category);

    List<CategoryResponse> toResponse(List<Category> category);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "categoryName", target = "name")
    void update(CategoryRequest categoryRequest, @MappingTarget Category category);
}
