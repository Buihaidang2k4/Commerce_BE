package com.example.MyShop_API.mapper;
import org.mapstruct.Mapping;

import com.example.MyShop_API.dto.response.ReviewResponse;
import com.example.MyShop_API.entity.ProductReview;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductReviewMapper {


    @Mapping(source = "id", target = "reviewId")
    @Mapping(source = "reviewerNameSnapshot", target = "customerName")
    @Mapping(source = "content", target = "comment")
    ReviewResponse toResponse(ProductReview review);
}
