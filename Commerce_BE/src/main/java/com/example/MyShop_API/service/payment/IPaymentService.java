package com.example.MyShop_API.service.payment;

import com.example.MyShop_API.Enum.PaymentMethod;
import com.example.MyShop_API.dto.response.PaymentDto;
import com.example.MyShop_API.dto.response.PaymentInitResponse;
import com.example.MyShop_API.dto.response.VnpayResponse;
import com.example.MyShop_API.entity.Order;
import com.example.MyShop_API.entity.Payment;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface IPaymentService {
    List<PaymentDto> getPayments();

    PaymentDto getPayment(Long paymentId);

    PaymentDto getPaymentByOrder(Long orderId);

    Payment getPaymentByOrderId(Long orderId);

    boolean processCashPayment(Long orderId);

    String createVnPayPayment(HttpServletRequest request, Long orderId, String bankCode);

    VnpayResponse handleVnPayCallback(HttpServletRequest request);

    void confirmCodPayment(Order order);

    Payment createPayment(Order order, PaymentMethod paymentMethod, long amount, String bankCode);

    PaymentInitResponse payOrderwithVnpay(Long orderId, HttpServletRequest request);

    void checkExpiredPayments();
}
