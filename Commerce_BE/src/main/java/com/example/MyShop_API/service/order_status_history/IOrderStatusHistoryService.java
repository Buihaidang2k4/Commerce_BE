package com.example.MyShop_API.service.order_status_history;

import com.example.MyShop_API.Enum.OrderStatus;
import com.example.MyShop_API.entity.Order;
import com.example.MyShop_API.entity.OrderStatusHistory;
import com.example.MyShop_API.entity.User;
import com.example.MyShop_API.entity.UserProfile;

import java.util.List;

public interface IOrderStatusHistoryService {

    List<OrderStatusHistory> getHistory(Long orderId);

    void logStatusChange(Order order, OrderStatus status, User user);
}
