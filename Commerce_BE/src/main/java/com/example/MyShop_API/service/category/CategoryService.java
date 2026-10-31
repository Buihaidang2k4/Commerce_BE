package com.example.MyShop_API.service.category;

import com.example.MyShop_API.dto.response.CategoryResponse;
import com.example.MyShop_API.entity.Category;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.constants.CommonMessageUtils;
import com.example.MyShop_API.mapper.CategoryMapper;
import com.example.MyShop_API.repo.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService implements ICategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategory() {
        return categoryRepository.findAll().stream().map(categoryMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoriesByCouponIds(List<Long> categoryIds) {
        List<Category> categories = categoryRepository.findAllById(categoryIds);
        return categoryMapper.toResponse(categories);
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() ->
                new AppException(CommonMessageUtils.CATEGORY_NOT_EXISTED));
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    public Category addCategory(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new AppException(CommonMessageUtils.CATEGORY_EXISTED);
        }
        category.setUpdatedAt(java.time.Instant.now());
        return categoryRepository.save(category);
    }

    public Category updateCategory(Category category, Long id) {
        Category oldCategory = getCategoryById(id);
        if (oldCategory == null) {
            throw new AppException(CommonMessageUtils.CATEGORY_NOT_EXISTED);
        }

        oldCategory.setName(category.getName());
        oldCategory.setDescription(category.getDescription());
        oldCategory.setUpdatedAt(java.time.Instant.now());
        return categoryRepository.save(oldCategory);
    }

    public void deleteCategoryById(Long id) {
        categoryRepository.findById(id).ifPresentOrElse(categoryRepository::delete, () -> {
            throw new AppException(CommonMessageUtils.CATEGORY_NOT_EXISTED);
        });
    }
}
