package com.example.MyShop_API.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder

public class ReviewResponse {
    private Long reviewId;
    private String customerName;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}
