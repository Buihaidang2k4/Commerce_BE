package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.request.CategoryRequest;
import com.example.MyShop_API.dto.response.CategoryResponse;
import com.example.MyShop_API.entity.Category;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toEntity(CategoryRequest categoryRequest);

    CategoryResponse toResponse(Category category);

    List<CategoryResponse> toResponse(List<Category> category);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(CategoryRequest categoryRequest, @MappingTarget Category category);
}
