package com.example.MyShop_API.service.payment;

import com.example.MyShop_API.constants.PaymentMethod;
import com.example.MyShop_API.dto.response.PaymentDto;
import com.example.MyShop_API.dto.response.PaymentInitResponse;
import com.example.MyShop_API.dto.response.VnpayResponse;
import com.example.MyShop_API.entity.Order;
import com.example.MyShop_API.entity.PaymentIntent;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface IPaymentIntentService {
    List<PaymentDto> getPayments();

    PaymentDto getPayment(Long paymentId);

    PaymentDto getPaymentByOrder(Long orderId);

    PaymentIntent getPaymentByOrderId(Long orderId);

    boolean processCashPayment(Long orderId);

    String createVnPayPayment(HttpServletRequest request, Long orderId, String bankCode);

    VnpayResponse handleVnPayCallback(HttpServletRequest request);

    void confirmCodPayment(Order order);

    PaymentIntent createPayment(Order order, String paymentMethod, long amount, String bankCode);

    PaymentInitResponse payOrderwithVnpay(Long orderId, HttpServletRequest request);

    void checkExpiredPayments();
}
