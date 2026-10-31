package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.OrderStatusTransition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderStatusTransitionRepository extends JpaRepository<OrderStatusTransition, Long> {
    List<OrderStatusTransition> findByOrderIdOrderByOccurredAtAsc(Long orderId);
    void deleteByOrderId(Long orderId);
}
