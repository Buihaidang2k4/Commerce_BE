package com.example.MyShop_API.controller;

import com.example.MyShop_API.constants.ApiPathConstants;

import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.entity.OrderStatusTransition;
import com.example.MyShop_API.service.order_status_history.IOrderStatusTransitionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiPathConstants.BASE_ORDER_STATUS_HISTORY)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderStatusTransitionController {
    IOrderStatusTransitionService historyService;

    @GetMapping(ApiPathConstants.ORDER_STATUS_HISTORY_ORDER_ID)
    ResponseEntity<ApiResponse<List<OrderStatusTransition>>> getHistory(@PathVariable Long orderId) {
        return ResponseEntity.ok(new ApiResponse(200, "history", historyService.getHistory(orderId)));
    }

}
