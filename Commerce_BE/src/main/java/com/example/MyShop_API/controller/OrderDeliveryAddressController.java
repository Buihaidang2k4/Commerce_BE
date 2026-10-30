package com.example.MyShop_API.controller;

import com.example.MyShop_API.constants.ApiPath;


import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.dto.response.OrderDeliveryAddressResponse;
import com.example.MyShop_API.mapper.OrderDeliveryAddressMapper;
import com.example.MyShop_API.service.order_delivery_address.IOrderDeliveryAddressService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPath.BASE_ORDER_DELIVERY_ADDRESS)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderDeliveryAddressController {
    IOrderDeliveryAddressService orderDeliveryAddressService;
    OrderDeliveryAddressMapper deliveryAddressMapper;

    @GetMapping(ApiPath.ORDER_DELIVERY_ADDRESS_ORDER_ORDER_ID)
    public ResponseEntity<ApiResponse<OrderDeliveryAddressResponse>> getOrderDeliveryAddress(@PathVariable Long orderId) {
        return ResponseEntity.ok(new ApiResponse<>(200, "success", deliveryAddressMapper.toResponse(orderDeliveryAddressService.getOrderDeliveryAddress(orderId))));
    }
}
