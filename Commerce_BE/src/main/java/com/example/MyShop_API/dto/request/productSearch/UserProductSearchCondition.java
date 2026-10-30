package com.example.MyShop_API.dto.request.productSearch;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserProductSearchCondition extends BaseProductSearchCondition {
    @DecimalMin(value = "0.0", inclusive = true, message = "minPrice must be >= 0")
    private BigDecimal minPrice;
    @DecimalMin(value = "0.0", inclusive = true, message = "maxPrice must be >= 0")
    private BigDecimal maxPrice;
    private Boolean hasDiscount;
    private Boolean bestSeller;
    @DecimalMin(value = "0.0", message = "rating must be >= 0")
    @DecimalMax(value = "5.0", message = "rating must be <= 5")
    private Double rating;
    @Size(max = 100, message = "origin must not exceed 100 characters")
    private String origin;
}
