package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.response.ReviewResponse;
import com.example.MyShop_API.entity.Review;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    ReviewResponse toResponse(Review review);
}
