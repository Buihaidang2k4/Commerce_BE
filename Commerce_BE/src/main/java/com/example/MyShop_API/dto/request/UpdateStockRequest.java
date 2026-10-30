package com.example.MyShop_API.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStockRequest {
    @NotNull
    private Long productId;
    @Min(0)
    private int newAvailability;
}
