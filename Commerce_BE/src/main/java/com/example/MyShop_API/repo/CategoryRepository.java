package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.Category;
import com.example.MyShop_API.mapper.CategoryMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsCategoriesByCategoryName(String categoryName);

    Category getCategoriesByCategoryName(String categoryName);

    Category findByCategoryName(String categoryName);
}
