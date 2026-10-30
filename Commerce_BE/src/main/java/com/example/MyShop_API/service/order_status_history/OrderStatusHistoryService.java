package com.example.MyShop_API.service.order_status_history;

import com.example.MyShop_API.constants.OrderStatus;
import com.example.MyShop_API.entity.Order;
import com.example.MyShop_API.entity.OrderStatusHistory;
import com.example.MyShop_API.entity.User;
import com.example.MyShop_API.entity.UserProfile;
import com.example.MyShop_API.repo.OrderStatusHistoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderStatusHistoryService implements IOrderStatusHistoryService {
    OrderStatusHistoryRepository historyRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OrderStatusHistory> getHistory(Long orderId) {
        return historyRepository.findByOrderIdOrderByChangedAtAsc(orderId);
    }

    // =============== LOG STATUS ORDER ======================
    @Override
    public void logStatusChange(Order order, String newStatus, User user) {
        OrderStatusHistory orderStatusHistory = OrderStatusHistory.builder()
                .orderId(order.getOrderId())
                .changedAt(LocalDateTime.now())
                .fromStatus(order.getOrderStatus())
                .orderStatus(newStatus)
                .changedByUserId(user == null ? null : user.getId())
                .build();

        historyRepository.save(orderStatusHistory);
    }
}
