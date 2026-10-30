package com.example.MyShop_API.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    Long cartItemId;
    Long productId;
    String productName;
    Integer quantity;
    BigDecimal unitPrice;
    BigDecimal totalPrice;
}
