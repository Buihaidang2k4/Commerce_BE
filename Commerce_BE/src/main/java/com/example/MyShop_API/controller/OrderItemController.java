package com.example.MyShop_API.controller;

import com.example.MyShop_API.constants.ApiPathConstants;

import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.dto.request.OrderItemRequest;
import com.example.MyShop_API.dto.response.OrderItemResponse;
import com.example.MyShop_API.service.order.IOrderItemService;
import com.example.MyShop_API.service.order.OrderItemService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPathConstants.BASE_ORDER_ITEM)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderItemController {
    IOrderItemService orderItemService;

    @GetMapping
    ApiResponse<List<OrderItemResponse>> getOrderItem() {
        return ApiResponse.<List<OrderItemResponse>>builder()
                .code(200)
                .message("OK")
                .data(orderItemService.getOrderItem())
                .build();
    }

    @GetMapping(ApiPathConstants.ORDER_ITEM_ORDER_ID)
    ApiResponse<OrderItemResponse> getOrderItem(@PathVariable Long orderId) {
        return ApiResponse.<OrderItemResponse>builder()
                .code(200)
                .message("OK")
                .data(orderItemService.getOrderItem(orderId))
                .build();
    }

    @PutMapping
    ApiResponse<OrderItemResponse> updateOrderItem(@Valid @RequestBody OrderItemRequest orderItemRequest, @RequestParam Long orderItemId) {
        return ApiResponse.<OrderItemResponse>builder()
                .code(200)
                .message("OK")
                .data(orderItemService.updateOrderItem(orderItemId, orderItemRequest))
                .build();
    }

    @DeleteMapping
    ApiResponse<Void> deleteOrderItem(@RequestParam Long orderItemId) {
        orderItemService.deleteOrderItem(orderItemId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("OK")
                .build();
    }
}
