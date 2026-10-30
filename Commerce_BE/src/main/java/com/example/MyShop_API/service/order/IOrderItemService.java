package com.example.MyShop_API.service.order;

import com.example.MyShop_API.dto.request.OrderItemRequest;
import com.example.MyShop_API.dto.response.OrderItemResponse;

import java.util.List;

public interface IOrderItemService {

    List<OrderItemResponse> getOrderItem();

    OrderItemResponse getOrderItem(Long orderItemId);

    OrderItemResponse updateOrderItem(Long orderItemId, OrderItemRequest orderItemRequest);

    void deleteOrderItem(Long orderItemId);
}
