package com.example.MyShop_API.service.payment;

import com.example.MyShop_API.constants.PaymentProviderStatusConstants;

import com.example.MyShop_API.repo.OrderItemRepository;

import com.example.MyShop_API.constants.OrderStatusConstants;
import com.example.MyShop_API.constants.PaymentMethodConstants;
import com.example.MyShop_API.constants.PaymentStatusConstants;
import com.example.MyShop_API.config.payment.VnpayConfig;
import com.example.MyShop_API.dto.request.OrderRequest;
import com.example.MyShop_API.dto.request.PaymentRequest;
import com.example.MyShop_API.dto.response.PaymentDto;
import com.example.MyShop_API.dto.response.PaymentInitResponse;
import com.example.MyShop_API.dto.response.PaymentResponse;
import com.example.MyShop_API.dto.response.VnpayResponse;
import com.example.MyShop_API.entity.Order;
import com.example.MyShop_API.entity.OrderItem;
import com.example.MyShop_API.entity.OrderStatusTransition;
import com.example.MyShop_API.entity.PaymentIntent;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.constants.CommonMessageConstants;
import com.example.MyShop_API.mapper.PaymentIntentMapper;
import com.example.MyShop_API.repo.OrderRepository;
import com.example.MyShop_API.repo.PaymentIntentRepository;
import com.example.MyShop_API.service.inventory.IStockBalanceService;
import com.example.MyShop_API.service.order_status_history.OrderStatusTransitionService;
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
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentIntentService implements IPaymentIntentService {
    private final OrderItemRepository orderItemRepository;
    VnpayConfig vnpayConfig;
    OrderRepository orderRepository;
    PaymentIntentRepository paymentRepository;
    PaymentIntentMapper paymentMapper;
    OrderStatusTransitionService historyService;
    IStockBalanceService inventoryService;

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> getPayments() {
        List<PaymentIntent> payments = paymentRepository.findAll();
        return payments.stream().map(paymentMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto getPayment(Long paymentId) {
        PaymentIntent payment = paymentRepository.findById(paymentId).orElseThrow(() -> new AppException(CommonMessageConstants.PAYMENT_NOT_EXISTED));
        return paymentMapper.toDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto getPaymentByOrder(Long orderId) {
        PaymentIntent payment = paymentRepository.findByOrderId(orderId).orElseThrow(() -> new AppException(CommonMessageConstants.PAYMENT_NOT_EXISTED));
        return paymentMapper.toDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentIntent getPaymentByOrderId(Long orderId) {
        PaymentIntent payment = paymentRepository.
                findByOrderId(orderId).orElseThrow(() -> new AppException(CommonMessageConstants.PAYMENT_NOT_EXISTED));
        return payment;
    }

    // ======================== COD =========================================
    @Override
    public boolean processCashPayment(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new AppException(CommonMessageConstants.ORDER_NOT_EXISTED)
        );

        PaymentIntent payment = PaymentIntent.builder()
                .orderId(order.getId())
                .providerStatus(PaymentProviderStatusConstants.PENDING)
                .paymentDate(Instant.now())
                .methodCode(PaymentMethodConstants.CASH)
                .status(PaymentStatusConstants.UNPAID)
                .amount(order.getTotalAmount())
                .vnpTxnRef(order.getId().toString())
                .build();

        paymentRepository.save(payment);
        order.setPaymentId(payment.getId());
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
            Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(CommonMessageConstants.ORDER_NOT_EXISTED));
            PaymentIntent payment = paymentRepository.findByOrderId(orderId).orElseThrow(() -> new AppException(CommonMessageConstants.PAYMENT_NOT_EXISTED));

            String transactionNo = request.getParameter("vnp_TransactionNo");
            String orderInfo = request.getParameter("vnp_OrderInfo");
            String cardType = request.getParameter("vnp_CardType");

            // Cập nhật bản ghi PaymentIntent khi thanh toán thành công
            payment.setStatus(PaymentStatusConstants.PAID);
            payment.setResponseCode(responseCode);
            payment.setProviderTransactionId(transactionNo);
            payment.setCardType(cardType);
            payment.setOrderInfo(orderInfo);
            payment.setProviderStatus(PaymentProviderStatusConstants.SUCCESS);
            payment.setPaymentDate(Instant.now());

            paymentRepository.save(payment);

            // Luu thanh toan vao dơn hang
            order.setPaymentId(payment.getId());
            orderRepository.save(order);
            return new VnpayResponse(
                    "00",
                    "PaymentIntent order: " + vnpTxnRef + " Success",
                    vnpTxnRef,
                    orderInfo,
                    PaymentStatusConstants.PAID,
                    PaymentMethodConstants.VNPAY
            );
        }
        // Nếu thanh toán thất bại
        return new VnpayResponse(responseCode,
                "PaymentIntent Failed",
                request.getParameter("vnp_TxnRef"),
                request.getParameter("vnp_OrderInfo"),
                PaymentStatusConstants.UNPAID,
                PaymentMethodConstants.VNPAY);
    }


    private long getTotalAmountFromOrder(long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(CommonMessageConstants.ORDER_NOT_EXISTED));

        BigDecimal totalAmount = order.getTotalAmount();

        // Làm tròn đúng, không mất dữ liệu
        return totalAmount.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }

    public void confirmCodPayment(Order order) {
        PaymentIntent payment = Optional.ofNullable(Optional.ofNullable(order.getPaymentId()).flatMap(paymentRepository::findById).orElse(null))
                .orElseThrow(() -> new AppException(CommonMessageConstants.PAYMENT_NOT_EXISTED));

        if (!PaymentMethodConstants.CASH.equals(payment.getMethodCode())) {
            throw new AppException(CommonMessageConstants.INVALID_PAYMENT_METHOD);
        }

        payment.setStatus(PaymentStatusConstants.PAID);
        payment.setProviderStatus(PaymentProviderStatusConstants.SUCCESS);
        payment.setPaymentDate(Instant.now());
    }


    // =============== create payment  ====================
    public PaymentIntent createPayment(
            Order order,
            String paymentMethod,
            long amount,
            String bankCode
    ) {

        if (paymentMethod == null || !PaymentMethodConstants.VALUES.contains(paymentMethod)) throw new AppException(CommonMessageConstants.INVALID_PAYMENT_METHOD);
        if (order.getId() == null) {
            throw new IllegalStateException("Order must be persisted before creating payment");
        }

        String status =
                PaymentMethodConstants.VNPAY.equals(paymentMethod)
                        ? PaymentStatusConstants.INIT
                        : PaymentStatusConstants.UNPAID;

        PaymentIntent payment = PaymentIntent.builder()
                .orderId(order.getId())
                .methodCode(paymentMethod)
                .status(status)
                .amount(BigDecimal.valueOf(amount))
                .vnpTxnRef(order.getId().toString())
                .bankCode(
                        (bankCode != null && !bankCode.isBlank())
                                ? bankCode
                                : null
                )
                .expiresAt(
                        PaymentMethodConstants.VNPAY.equals(paymentMethod)
                                ? Instant.now().plus(12, java.time.temporal.ChronoUnit.HOURS)
                                : null
                )
                .providerStatus("")
                .build();

        paymentRepository.save(payment);
        return payment;
    }


    // ================ payment vnpay =================
    @Override
    @Transactional
    public PaymentInitResponse payOrderwithVnpay(Long orderId, HttpServletRequest request) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(CommonMessageConstants.ORDER_NOT_EXISTED));

        PaymentIntent payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new AppException(CommonMessageConstants.PAYMENT_NOT_EXISTED));

        if (OrderStatusConstants.CANCELLED.equals(order.getStatus())) {
            throw new AppException(CommonMessageConstants.ORDER_ALREADY_CANCELLED);
        }

        if (!PaymentStatusConstants.INIT.equals(payment.getStatus())) {
            throw new AppException(CommonMessageConstants.PAYMENT_ALREADY_PROCESSED);
        }

        if (payment.getExpiresAt().isBefore(Instant.now())) {
            throw new AppException(CommonMessageConstants.PAYMENT_EXPIRED);
        }

        if (!PaymentMethodConstants.VNPAY.equals(payment.getMethodCode())) {
            throw new AppException(CommonMessageConstants.INVALID_PAYMENT_METHOD);
        }

        if (PaymentStatusConstants.EXPIRED.equals(payment.getStatus())) {
            throw new AppException(CommonMessageConstants.PAYMENT_EXPIRED);
        }

        if (PaymentStatusConstants.PAID.equals(payment.getStatus())) {
            throw new AppException(CommonMessageConstants.PAYMENT_ALREADY_PROCESSED);
        }


        String paymentUrl = createVnpayUrl(order, payment, request);

        return new PaymentInitResponse(
                PaymentMethodConstants.VNPAY,
                paymentUrl
        );
    }

    // ====== check order with payment expired =======
    @Override
    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void checkExpiredPayments() {
        log.info("=== start check expired payments ===");
        Instant now = Instant.now();

        List<PaymentIntent> expiredPayments =
                paymentRepository.findByStatusInAndExpiresAtBefore(
                        List.of(PaymentStatusConstants.INIT, PaymentStatusConstants.FAILED),
                        now
                );

        if (expiredPayments.isEmpty()) {
            return;
        }

        for (PaymentIntent payment : expiredPayments) {

            Order order = Optional.ofNullable(payment.getOrderId()).flatMap(orderRepository::findById).orElse(null);

            // idempotent safety
            if (PaymentStatusConstants.EXPIRED.equals(payment.getStatus()) ||
                    OrderStatusConstants.CANCELLED.equals(order.getStatus())) {
                continue;
            }

            // ===== UPDATE PAYMENT =====
            payment.setStatus(PaymentStatusConstants.EXPIRED);
            payment.setProviderStatus(PaymentProviderStatusConstants.EXPIRED);

            // ===== UPDATE ORDER =====
            order.setStatus(OrderStatusConstants.CANCELLED);

            historyService.logStatusChange(order, OrderStatusConstants.CANCELLED, null);

            // ===== RESTOCK INVENTORY =====
            Optional.ofNullable(order.getId()).map(orderItemRepository::findByOrderId).orElseGet(List::of).forEach(item ->
                    inventoryService.restock(
                            item.getProductId(),
                            item.getQuantity()
                    )
            );

            log.info("Order {} expired & cancelled due to payment timeout", order.getId());
        }

        // flush batch
        paymentRepository.saveAll(expiredPayments);
    }

    private String createVnpayUrl(Order order, PaymentIntent payment, HttpServletRequest request) {
        long amount = getTotalAmountFromOrder(order.getId());

        Map<String, String> vnParamsMap = vnpayConfig.getVNPayConfig();
        vnParamsMap.put("vnp_Amount", String.valueOf(amount));
        vnParamsMap.put("vnp_BankCode", payment.getBankCode());
        vnParamsMap.put("vnp_IpAddr", VnpayUtil.getIpAddress(request));
        // set orderId - mã đơn hàng
        vnParamsMap.put("vnp_TxnRef", String.valueOf(order.getId()));

        // query url
        String queryUrl = VnpayUtil.getPaymentURL(vnParamsMap, true);
        String hashData = VnpayUtil.getPaymentURL(vnParamsMap, false);
        String vnpSecureHash = VnpayUtil.hmacSHA512(vnpayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnpayConfig.getVnp_PayUrl() + "?" + queryUrl;

        return paymentUrl;
    }

}
