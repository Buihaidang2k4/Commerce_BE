package com.example.MyShop_API.repo;

import com.example.MyShop_API.constants.PaymentStatusConstants;
import com.example.MyShop_API.entity.PaymentIntent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentIntentRepository extends JpaRepository<PaymentIntent, Long> {
    Optional<PaymentIntent> findById(Long paymentId);

    Optional<PaymentIntent> findByOrderId(Long orderOrderId);

    List<PaymentIntent> findByStatusInAndExpiresAtBefore(List<String> paymentStatuses, Instant now);
}
