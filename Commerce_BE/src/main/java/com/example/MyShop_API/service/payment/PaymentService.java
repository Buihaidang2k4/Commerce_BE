package com.example.MyShop_API.service.payment;

import com.example.MyShop_API.constants.OrderStatus;
import com.example.MyShop_API.constants.PaymentMethod;
import com.example.MyShop_API.constants.PaymentStatus;
import com.example.MyShop_API.config.payment.VnpayConfig;
import com.example.MyShop_API.dto.request.OrderRequest;
import com.example.MyShop_API.dto.request.PaymentRequest;
import com.example.MyShop_API.dto.response.PaymentDto;
import com.example.MyShop_API.dto.response.PaymentInitResponse;
import com.example.MyShop_API.dto.response.PaymentResponse;
import com.example.MyShop_API.dto.response.VnpayResponse;
import com.example.MyShop_API.entity.Order;
import com.example.MyShop_API.entity.OrderItem;
import com.example.MyShop_API.entity.OrderStatusHistory;
import com.example.MyShop_API.entity.Payment;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.constants.CommonMessageUtils;
import com.example.MyShop_API.mapper.PaymentMapper;
import com.example.MyShop_API.repo.OrderRepository;
import com.example.MyShop_API.repo.PaymentRepository;
import com.example.MyShop_API.service.inventory.IInventoryService;
import com.example.MyShop_API.service.order_status_history.OrderStatusHistoryService;
import com.example.MyShop_API.utils.VnpayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService implements IPaymentService {
    private final com.example.MyShop_API.service.model.ModelLookup models;
    VnpayConfig vnpayConfig;
    OrderRepository orderRepository;
    PaymentRepository paymentRepository;
    PaymentMapper paymentMapper;
    OrderStatusHistoryService historyService;
    IInventoryService inventoryService;

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> getPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream().map(paymentMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto getPayment(Long paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId).orElseThrow(() -> new AppException(CommonMessageUtils.PAYMENT_NOT_EXISTED));
        return paymentMapper.toDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto getPaymentByOrder(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(() -> new AppException(CommonMessageUtils.PAYMENT_NOT_EXISTED));
        return paymentMapper.toDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Payment getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.
                findByOrderId(orderId).orElseThrow(() -> new AppException(CommonMessageUtils.PAYMENT_NOT_EXISTED));
        return payment;
    }

    // ======================== COD =========================================
    @Override
    public boolean processCashPayment(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new AppException(CommonMessageUtils.ORDER_NOT_EXISTED)
        );

        Payment payment = Payment.builder()
                .orderId(order.getOrderId())
                .status("PENDING")
                .paymentDate(LocalDateTime.now())
                .paymentMethod(PaymentMethod.CASH)
                .paymentStatus(PaymentStatus.UNPAID)
                .amount(order.getTotalAmount().longValue())
                .vnpTxnRef(order.getOrderId().toString())
                .build();

        paymentRepository.save(payment);
        order.setPaymentId(payment.getPaymentId());
        orderRepository.save(order);
        return true;
    }

    // ================================== VnPay =======================================
    @Override
    public String createVnPayPayment(HttpServletRequest request, Long orderId, String bankCode) {
        long amount = getTotalAmountFromOrder(orderId);

        Map<String, String> vnParamsMap = vnpayConfig.getVNPayConfig();
        vnParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnParamsMap.put("vnp_BankCode", bankCode);
        }
        vnParamsMap.put("vnp_IpAddr", VnpayUtil.getIpAddress(request));
        // set orderId - mã đơn hàng
        vnParamsMap.put("vnp_TxnRef", String.valueOf(orderId));

        // query url
        String queryUrl = VnpayUtil.getPaymentURL(vnParamsMap, true);
        String hashData = VnpayUtil.getPaymentURL(vnParamsMap, false);
        String vnpSecureHash = VnpayUtil.hmacSHA512(vnpayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnpayConfig.getVnp_PayUrl() + "?" + queryUrl;

        return paymentUrl;
    }

    // ============== response payment vnpay ================
    @Override
    public VnpayResponse handleVnPayCallback(HttpServletRequest request) {
        String responseCode = request.getParameter("vnp_ResponseCode");

        // Nếu thanh toán thành công
        if ("00".equals(responseCode)) {
            String vnpTxnRef = request.getParameter("vnp_TxnRef");
            Long orderId = Long.parseLong(request.getParameter("vnp_TxnRef"));
            Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(CommonMessageUtils.ORDER_NOT_EXISTED));
            Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(() -> new AppException(CommonMessageUtils.PAYMENT_NOT_EXISTED));

            String transactionNo = request.getParameter("vnp_TransactionNo");
            String orderInfo = request.getParameter("vnp_OrderInfo");
            String cardType = request.getParameter("vnp_CardType");

            // Cập nhật bản ghi Payment khi thanh toán thành công
            payment.setPaymentStatus(PaymentStatus.PAID);
            payment.setResponseCode(responseCode);
            payment.setTransactionNo(transactionNo);
            payment.setCardType(cardType);
            payment.setOrderInfo(orderInfo);
            payment.setStatus("SUCCESS");
            payment.setPaymentDate(LocalDateTime.now());

            paymentRepository.save(payment);

            // Luu thanh toan vao dơn hang
            order.setPaymentId(payment.getPaymentId());
            orderRepository.save(order);
            return new VnpayResponse(
                    "00",
                    "Payment order: " + vnpTxnRef + " Success",
                    vnpTxnRef,
                    orderInfo,
                    PaymentStatus.PAID,
                    PaymentMethod.VNPAY
            );
        }
        // Nếu thanh toán thất bại
        return new VnpayResponse(responseCode,
                "Payment Failed",
                request.getParameter("vnp_TxnRef"),
                request.getParameter("vnp_OrderInfo"),
                PaymentStatus.UNPAID,
                PaymentMethod.VNPAY);
    }


    private long getTotalAmountFromOrder(long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(CommonMessageUtils.ORDER_NOT_EXISTED));

        BigDecimal totalAmount = order.getTotalAmount();

        // Làm tròn đúng, không mất dữ liệu
        return totalAmount.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }

    public void confirmCodPayment(Order order) {
        Payment payment = Optional.ofNullable(models.payment(order.getPaymentId()))
                .orElseThrow(() -> new AppException(CommonMessageUtils.PAYMENT_NOT_EXISTED));

        if (!PaymentMethod.CASH.equals(payment.getPaymentMethod())) {
            throw new AppException(CommonMessageUtils.INVALID_PAYMENT_METHOD);
        }

        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setStatus("SUCCESS");
        payment.setPaymentDate(LocalDateTime.now());
    }


    // =============== create payment  ====================
    public Payment createPayment(
            Order order,
            String paymentMethod,
            long amount,
            String bankCode
    ) {

        if (paymentMethod == null || !PaymentMethod.VALUES.contains(paymentMethod)) throw new AppException(CommonMessageUtils.INVALID_PAYMENT_METHOD);
        if (order.getOrderId() == null) {
            throw new IllegalStateException("Order must be persisted before creating payment");
        }

        String status =
                PaymentMethod.VNPAY.equals(paymentMethod)
                        ? PaymentStatus.INIT
                        : PaymentStatus.UNPAID;

        Payment payment = Payment.builder()
                .orderId(order.getOrderId())
                .paymentMethod(paymentMethod)
                .paymentStatus(status)
                .amount(amount)
                .vnpTxnRef(order.getOrderId().toString())
                .bankCode(
                        (bankCode != null && !bankCode.isBlank())
                                ? bankCode
                                : null
                )
                .expiredAt(
                        PaymentMethod.VNPAY.equals(paymentMethod)
                                ? LocalDateTime.now().plusHours(12)
                                : null
                )
                .status("")
                .build();

        paymentRepository.save(payment);
        return payment;
    }


    // ================ payment vnpay =================
    @Override
    @Transactional
    public PaymentInitResponse payOrderwithVnpay(Long orderId, HttpServletRequest request) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(CommonMessageUtils.ORDER_NOT_EXISTED));

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new AppException(CommonMessageUtils.PAYMENT_NOT_EXISTED));

        if (OrderStatus.CANCELLED.equals(order.getOrderStatus())) {
            throw new AppException(CommonMessageUtils.ORDER_ALREADY_CANCELLED);
        }

        if (!PaymentStatus.INIT.equals(payment.getPaymentStatus())) {
            throw new AppException(CommonMessageUtils.PAYMENT_ALREADY_PROCESSED);
        }

        if (payment.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new AppException(CommonMessageUtils.PAYMENT_EXPIRED);
        }

        if (!PaymentMethod.VNPAY.equals(payment.getPaymentMethod())) {
            throw new AppException(CommonMessageUtils.INVALID_PAYMENT_METHOD);
        }

        if (PaymentStatus.EXPIRED.equals(payment.getPaymentStatus())) {
            throw new AppException(CommonMessageUtils.PAYMENT_EXPIRED);
        }

        if (PaymentStatus.PAID.equals(payment.getPaymentStatus())) {
            throw new AppException(CommonMessageUtils.PAYMENT_ALREADY_PROCESSED);
        }


        String paymentUrl = createVnpayUrl(order, payment, request);

        return new PaymentInitResponse(
                PaymentMethod.VNPAY,
                paymentUrl
        );
    }

    // ====== check order with payment expired =======
    @Override
    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void checkExpiredPayments() {
        log.info("=== start check expired payments ===");
        LocalDateTime now = LocalDateTime.now();

        List<Payment> expiredPayments =
                paymentRepository.findByPaymentStatusInAndExpiredAtBefore(
                        List.of(PaymentStatus.INIT, PaymentStatus.FAILED),
                        now
                );

        if (expiredPayments.isEmpty()) {
            return;
        }

        for (Payment payment : expiredPayments) {

            Order order = models.order(payment.getOrderId());

            // idempotent safety
            if (PaymentStatus.EXPIRED.equals(payment.getPaymentStatus()) ||
                    OrderStatus.CANCELLED.equals(order.getOrderStatus())) {
                continue;
            }

            // ===== UPDATE PAYMENT =====
            payment.setPaymentStatus(PaymentStatus.EXPIRED);
            payment.setStatus("EXPIRED");

            // ===== UPDATE ORDER =====
            order.setOrderStatus(OrderStatus.CANCELLED);

            historyService.logStatusChange(order, OrderStatus.CANCELLED, null);

            // ===== RESTOCK INVENTORY =====
            models.orderItems(order.getOrderId()).forEach(item ->
                    inventoryService.restock(
                            item.getProductId(),
                            item.getQuantity()
                    )
            );

            log.info("Order {} expired & cancelled due to payment timeout", order.getOrderId());
        }

        // flush batch
        paymentRepository.saveAll(expiredPayments);
    }

    private String createVnpayUrl(Order order, Payment payment, HttpServletRequest request) {
        long amount = getTotalAmountFromOrder(order.getOrderId());

        Map<String, String> vnParamsMap = vnpayConfig.getVNPayConfig();
        vnParamsMap.put("vnp_Amount", String.valueOf(amount));
        vnParamsMap.put("vnp_BankCode", payment.getBankCode());
        vnParamsMap.put("vnp_IpAddr", VnpayUtil.getIpAddress(request));
        // set orderId - mã đơn hàng
        vnParamsMap.put("vnp_TxnRef", String.valueOf(order.getOrderId()));

        // query url
        String queryUrl = VnpayUtil.getPaymentURL(vnParamsMap, true);
        String hashData = VnpayUtil.getPaymentURL(vnParamsMap, false);
        String vnpSecureHash = VnpayUtil.hmacSHA512(vnpayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnpayConfig.getVnp_PayUrl() + "?" + queryUrl;

        return paymentUrl;
    }

}
