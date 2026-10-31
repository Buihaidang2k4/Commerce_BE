package com.example.MyShop_API.service.order_status_history;

import com.example.MyShop_API.constants.OrderStatus;
import com.example.MyShop_API.entity.Order;
import com.example.MyShop_API.entity.OrderStatusTransition;
import com.example.MyShop_API.entity.UserAccount;
import com.example.MyShop_API.entity.UserProfile;
import com.example.MyShop_API.repo.OrderStatusTransitionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderStatusTransitionService implements IOrderStatusTransitionService {
    OrderStatusTransitionRepository historyRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OrderStatusTransition> getHistory(Long orderId) {
        return historyRepository.findByOrderIdOrderByOccurredAtAsc(orderId);
    }

    // =============== LOG STATUS ORDER ======================
    @Override
    public void logStatusChange(Order order, String newStatus, UserAccount user) {
        OrderStatusTransition orderStatusHistory = OrderStatusTransition.builder()
                .orderId(order.getId())
                .occurredAt(Instant.now())
                .fromStatus(order.getStatus())
                .toStatus(newStatus)
                .actorUserId(user == null ? null : user.getId())
                .build();

        historyRepository.save(orderStatusHistory);
    }
}
