package com.example.MyShop_API.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReserveStockRequest {
    Long productId;
    @Min(1)
    int quantity;
}
