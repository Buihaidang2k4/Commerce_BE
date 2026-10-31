package com.example.MyShop_API.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CreateReviewRequest {

    Instant createdAt = Instant.now();
    @Min(1)
    @Max(5)
    private Integer rating;
    private String comment;

}
