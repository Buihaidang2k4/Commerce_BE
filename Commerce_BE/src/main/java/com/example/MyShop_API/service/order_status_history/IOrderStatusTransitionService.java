package com.example.MyShop_API.service.order_status_history;

import com.example.MyShop_API.constants.OrderStatusConstants;
import com.example.MyShop_API.entity.Order;
import com.example.MyShop_API.entity.OrderStatusTransition;
import com.example.MyShop_API.entity.UserAccount;
import com.example.MyShop_API.entity.UserProfile;

import java.util.List;

public interface IOrderStatusTransitionService {

    List<OrderStatusTransition> getHistory(Long orderId);

    void logStatusChange(Order order, String status, UserAccount user);
}
