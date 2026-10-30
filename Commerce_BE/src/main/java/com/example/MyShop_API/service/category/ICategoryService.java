package com.example.MyShop_API.service.category;

import com.example.MyShop_API.dto.request.CategoryRequest;
import com.example.MyShop_API.dto.response.CategoryResponse;
import com.example.MyShop_API.entity.Category;

import java.util.List;

public interface ICategoryService {
    List<CategoryResponse> getAllCategory();

    List<CategoryResponse> getCategoriesByCouponIds(List<Long> categoryIds);

    Category getCategoryById(Long id);

    Category getCategoryByName(String name);

    Category addCategory(Category category);

    Category updateCategory(Category category, Long id);

    void deleteCategoryById(Long id);
}
