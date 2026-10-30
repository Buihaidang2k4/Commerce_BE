package com.example.MyShop_API.repo;

import com.example.MyShop_API.Enum.PaymentStatus;
import com.example.MyShop_API.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentId(Long paymentId);

    Optional<Payment> findByOrder_OrderId(Long orderOrderId);

    List<Payment> findByPaymentStatusInAndExpiredAtBefore(List<PaymentStatus> paymentStatuses, LocalDateTime now);
}
