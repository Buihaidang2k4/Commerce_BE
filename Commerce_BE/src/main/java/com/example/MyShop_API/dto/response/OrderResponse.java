package com.example.MyShop_API.dto.response;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    Long orderId;
    Long paymentId;
    Long profileId;
    Long deliveryAddressId;
    BigDecimal shippingFee;
    BigDecimal discountAmount;
    BigDecimal totalAmount;
    String orderStatus;
    LocalDate orderDate;
    List<OrderItemResponse> orderItemResponses;
    CouponResponse couponResponse;
}
