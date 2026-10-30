package com.example.MyShop_API.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    Long categoryId;
    String categoryName;
    String description;
    LocalDate createAt;
    LocalDate updateAt;
}
