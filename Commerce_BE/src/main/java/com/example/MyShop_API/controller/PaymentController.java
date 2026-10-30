package com.example.MyShop_API.controller;

import com.example.MyShop_API.constants.ApiPath;

import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.dto.response.PaymentDto;
import com.example.MyShop_API.dto.response.VnpayResponse;
import com.example.MyShop_API.service.order.IOrderService;
import com.example.MyShop_API.service.payment.IPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;


@Slf4j
@RestController
@RequestMapping(ApiPath.BASE_PAYMENT)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {
    IPaymentService paymentService;
    IOrderService orderService;

    @NonFinal
    @Value("${app.frontend.baseUrl}")
    String baseUrlFe;

    @GetMapping
    ResponseEntity<ApiResponse<List<PaymentDto>>> getPayments() {
        return ResponseEntity.ok(new ApiResponse<>(200, "success", paymentService.getPayments()));
    }

    @GetMapping(ApiPath.PAYMENT_PAYMENT_ID)
    ResponseEntity<ApiResponse<PaymentDto>> getPayment(@PathVariable Long paymentId) {
        return ResponseEntity.ok(new ApiResponse<>(200, "success", paymentService.getPayment(paymentId)));
    }

    @GetMapping(ApiPath.PAYMENT_ORDER_ORDER_ID)
    ResponseEntity<ApiResponse<PaymentDto>> getPaymentByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(new ApiResponse<>(200, "success", paymentService.getPaymentByOrder(orderId)));
    }

    // ================ Create Url payment =================
    @GetMapping(ApiPath.PAYMENT_VN_PAY)
    @Operation(summary = "test")
    public ResponseEntity<ApiResponse<String>> pay(HttpServletRequest request,
                                                   @RequestParam Long orderId,
                                                   @RequestParam String bankCode
    ) {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Success", paymentService.createVnPayPayment(request, orderId, bankCode)));
    }


    @GetMapping(ApiPath.PAYMENT_VN_PAY_CALLBACK)
    public void vnpayCallback(HttpServletRequest request,
                              HttpServletResponse response) throws IOException {

        String orderId = request.getParameter("vnp_TxnRef");

        orderService.finalizeVnPayCallback(request);

        String redirectUrl = UriComponentsBuilder
                .fromHttpUrl(baseUrlFe + orderId)
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }


    // ===================== PAYMENT CASH ======================
    @PostMapping(ApiPath.PAYMENT_COD_ORDER_ID)
    @Operation(summary = "test")
    ResponseEntity<ApiResponse<?>> payWithCash(@PathVariable Long orderId) {
        return ResponseEntity.ok(new ApiResponse(200, "pay success", paymentService.processCashPayment(orderId)));
    }


    // ===================== PAYMENT VNPAY ======================
    @PostMapping(ApiPath.PAYMENT_VNPAY_COMFIRM_ORDER_ID)
    @Operation(summary = "pay for the order vnpay")
    ResponseEntity<ApiResponse<?>> payWithVnpay(@PathVariable Long orderId, HttpServletRequest request) {
        return ResponseEntity.ok(new ApiResponse(200, "pay success", paymentService.payOrderwithVnpay(orderId, request)));
    }


}

