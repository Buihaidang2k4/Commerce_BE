package com.example.MyShop_API.dto.response;

import com.example.MyShop_API.entity.CartItem;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {
    Long cartId;
    List<CartItemResponse> items = new ArrayList<>();
    BigDecimal totalPrice = BigDecimal.ZERO;
}
