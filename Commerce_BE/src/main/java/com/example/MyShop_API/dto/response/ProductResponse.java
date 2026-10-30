package com.example.MyShop_API.dto.response;

import com.example.MyShop_API.dto.request.ImageDTO;
import com.example.MyShop_API.dto.request.InventoryDTO;
import com.example.MyShop_API.entity.Category;
import com.example.MyShop_API.entity.Image;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    Long productId;
    String productName;
    String description;
    BigDecimal price;
    BigDecimal discount;
    BigDecimal specialPrice;
    String bio;
    String slug;
    Double height;
    Double length;
    Double weight;
    Double width;
    String origin;
    Long soldCount;
    Integer reviewCount;
    Double avgRating;
    LocalDate createAt;
    LocalDate updateAt;
    Category category;
    InventoryDTO inventory;
    List<ImageDTO> images;
}
