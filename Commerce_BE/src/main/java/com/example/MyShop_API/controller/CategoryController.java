package com.example.MyShop_API.controller;

import com.example.MyShop_API.constants.ApiPathConstants;

import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.dto.request.CategoryRequest;
import com.example.MyShop_API.dto.response.CategoryResponse;
import com.example.MyShop_API.entity.Category;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.mapper.CategoryMapper;
import com.example.MyShop_API.service.category.CategoryService;
import com.example.MyShop_API.service.category.ICategoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestController
@RequestMapping(ApiPathConstants.BASE_CATEGORY)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {
    ICategoryService categoryService;
    CategoryMapper categoryMapper;

    @GetMapping(ApiPathConstants.CATEGORY_ALL)
    ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        try {
            List<CategoryResponse> categories = categoryService.getAllCategory();
            return ResponseEntity.ok(new ApiResponse<>(200, "success", categories));
        } catch (AppException e) {
            throw e;
        }

    }

    @GetMapping
    @Operation(summary = "get by list categoryIds")
    ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategoriesByCategoryIds(@RequestParam List<Long> categoryIds) {
        try {
            return ResponseEntity.ok(new ApiResponse<>(200, "success", categoryService.getCategoriesByCouponIds(categoryIds)));
        } catch (AppException e) {
            throw e;
        }

    }

    @GetMapping(ApiPathConstants.CATEGORY_CATEGORY_CATEGORY_ID)
    ResponseEntity<ApiResponse<CategoryResponse>> getCategory(@PathVariable Long categoryId) {
        try {
            Category category = categoryService.getCategoryById(categoryId);
            return ResponseEntity.ok(new ApiResponse<>(200, "success", categoryMapper.toResponse(category)));
        } catch (AppException e) {
            throw e;
        }
    }

    @GetMapping(ApiPathConstants.CATEGORY_CATEGORY_CATEGORY_NAME_NAME)
    ResponseEntity<ApiResponse> getCategory(@PathVariable String categoryName) {
        try {
            Category category = categoryService.getCategoryByName(categoryName);
            return ResponseEntity.ok(new ApiResponse<>(200, "success", categoryMapper.toResponse(category)));
        } catch (AppException e) {
            throw e;
        }
    }

    @PostMapping(ApiPathConstants.CATEGORY_CATEGORY_ADD)
    ResponseEntity<ApiResponse<CategoryResponse>> addCategory(@RequestBody Category category) {
        try {
            Category theCategory = categoryService.addCategory(category);
            return ResponseEntity.ok(new ApiResponse<>(200, "Add success", categoryMapper.toResponse(theCategory)));
        } catch (AppException e) {
            throw e;
        }

    }

    @PutMapping(ApiPathConstants.CATEGORY_CATEGORY_CATEGORY_ID_UPDATE)
    ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(@RequestBody Category category, @PathVariable Long categoryId) {
        try {
            Category updateCategory = categoryService.updateCategory(category, categoryId);
            return ResponseEntity.ok(new ApiResponse<>(200, "Update success", categoryMapper.toResponse(updateCategory)));
        } catch (AppException e) {
            throw e;
        }

    }

    @DeleteMapping(ApiPathConstants.CATEGORY_CATEGORY_CATEGORY_ID_DELETE)
    ResponseEntity<ApiResponse> deleteCategory(@PathVariable Long categoryId) {
        try {
            categoryService.deleteCategoryById(categoryId);
            return ResponseEntity.ok(new ApiResponse<>(200, "Delete success", null));
        } catch (AppException e) {
            throw e;
        }

    }
}
