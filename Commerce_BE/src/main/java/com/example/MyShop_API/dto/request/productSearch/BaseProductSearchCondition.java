package com.example.MyShop_API.dto.request.productSearch;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public abstract class BaseProductSearchCondition {
    @Size(max = 255, message = "keyword must not exceed 255 characters")
    protected String keyword;
    @Size(max = 100, message = "categoryName must not exceed 100 characters")
    protected String categoryName;
}
