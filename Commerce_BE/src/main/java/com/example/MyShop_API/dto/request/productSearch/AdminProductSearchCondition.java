package com.example.MyShop_API.dto.request.productSearch;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AdminProductSearchCondition extends BaseProductSearchCondition {
    @DecimalMin(value = "0.0", inclusive = true, message = "minPrice must be >= 0")
    private BigDecimal minPrice;
    @DecimalMin(value = "0.0", inclusive = true, message = "maxPrice must be >= 0")
    private BigDecimal maxPrice;
    private Boolean hasDiscount;
    private Boolean bestSeller;
    private Boolean inStock;
    private LocalDate fromDate;
    private LocalDate toDate;
}
