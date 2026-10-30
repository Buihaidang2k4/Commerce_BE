package com.example.MyShop_API.service.order;

import com.example.MyShop_API.constants.OrderStatus;
import com.example.MyShop_API.constants.PaymentMethod;
import com.example.MyShop_API.constants.PaymentStatus;
import com.example.MyShop_API.anotation.AdminOnly;
import com.example.MyShop_API.dto.request.OrderPlaceListItemRequest;
import com.example.MyShop_API.dto.request.OrderRequest;
import com.example.MyShop_API.dto.request.PlaceOrderFromCartRequest;
import com.example.MyShop_API.dto.response.OrderResponse;
import com.example.MyShop_API.dto.response.VnpayResponse;
import com.example.MyShop_API.entity.*;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.constants.CommonMessageUtils;
import com.example.MyShop_API.mapper.OrderMapper;
import com.example.MyShop_API.repo.*;
import com.example.MyShop_API.service.cart.ICartService;
import com.example.MyShop_API.service.coupon.ICouponService;
import com.example.MyShop_API.service.inventory.IInventoryService;
import com.example.MyShop_API.service.order_delivery_address.IOrderDeliveryAddressService;
import com.example.MyShop_API.service.order_status_history.IOrderStatusHistoryService;
import com.example.MyShop_API.service.payment.IPaymentService;
import com.example.MyShop_API.service.user.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService implements IOrderService {
    private final com.example.MyShop_API.service.model.ModelLookup models;
    OrderRepository orderRepository;
    ICartService cartService;
    ProductRepository productRepository;
    IInventoryService inventoryService;
    IPaymentService paymentService;
    UserProfileRepository profileRepository;
    ICouponService couponService;
    IOrderStatusHistoryService historyService;
    PaymentRepository paymentRepository;
    IOrderDeliveryAddressService deliveryAddressService;
    AddressRepository addressRepository;
    OrderMapper orderMapper;
    IUserService userService;
    CartItemRepository cartItemRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrders() {
        return orderRepository.findAll().stream().toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrder(Long orderId) {
        log.info("getOrderById().........");
        return orderRepository.findById(orderId).orElseThrow(() -> new AppException(CommonMessageUtils.ORDER_NOT_EXISTED));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByOrderStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> getOrdersByStatus(String status, Pageable pageable) {
        return orderRepository.findByOrderStatus(status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getUserOrders(Long profileId) {
        return orderRepository.findByProfileId(profileId);
    }

    // placeOrder Buy Now -> create orderItem -> Payment
    // ===================== Buy Now =============================
    @Override
    @Transactional
    public OrderResponse buyNow(OrderRequest orderRequest) {
        Product product = productRepository.findById(orderRequest.getProductId())
                .orElseThrow(() -> new AppException(CommonMessageUtils.PRODUCT_NOT_EXISTED));

        UserProfile profile = profileRepository.findById(orderRequest.getProfileId())
                .orElseThrow(() -> new AppException(CommonMessageUtils.PROFILE_NOT_EXISTED));

        if (!inventoryService.reserveStock(product.getProductId(), orderRequest.getQuantity())) {
            throw new AppException(CommonMessageUtils.INVENTORY_NOT_ENOUGH, models.inventory(product.getProductId()).getAvailable(), orderRequest.getQuantity());
        }

        String status = PaymentMethod.VNPAY.equals(orderRequest.getPaymentMethod())
                ? OrderStatus.CREATED
                : OrderStatus.PENDING;

        Order order = Order.builder()
                .profileId(profile.getProfileId())
                .orderDate(LocalDate.now())
                .orderStatus(status)
                .build();

        OrderItem item = OrderItem.builder()
                .productId(product.getProductId())
                .quantity(orderRequest.getQuantity())
                .price(product.getSpecialPrice() != null
                        ? product.getSpecialPrice()
                        : product.getPrice())
                .build();


        order.setUserId(profile.getUserId());
        orderRepository.save(order);
        item.setOrderId(order.getOrderId());
        item.setProductTitle(product.getProductName());
        models.getOrderItems().save(item);

        calculateTotalAmount(
                order,
                orderRequest.getShippingFee(),
                orderRequest.getCouponCode(),
                profile
        );

        setShippingOrder(
                order,
                orderRequest.getAddressId(),
                orderRequest.getProfileId(),
                orderRequest.getOrderNote()
        );

        orderRepository.save(order);
        Payment payment = paymentService.createPayment(
                order,
                orderRequest.getPaymentMethod(),
                order.getTotalAmount().longValue(),
                orderRequest.getBankCode()
        );

        order.setPaymentId(payment.getPaymentId());

        orderRepository.save(order);

        historyService.logStatusChange(order, status, null);

        return orderMapper.toResponse(order);
    }

    // ============== PLACE ORDER FROM LIST CART ITEMS (Dành cho tích chọn sản phẩm giỏ hàng) ==================
    @Override
    @Transactional
    public OrderResponse placeOrderFromListCartItems(OrderPlaceListItemRequest orderRequest) {
        log.info("====================== START PLACE ORDER FROM LIST CART ITEMS ======================");
        // tim profile
        UserProfile profile = profileRepository.findById(orderRequest.getProfileId())
                .orElseThrow(() -> new AppException(CommonMessageUtils.PROFILE_NOT_EXISTED));

        // lấy items người dùng đã chọn
        List<CartItem> cartItems = cartItemRepository.lockCartItems(orderRequest.getListItemId());

        Cart selectedCart = models.cartForProfile(profile.getProfileId());
        if (selectedCart == null || cartItems.size() != orderRequest.getListItemId().stream().distinct().count() || cartItems.stream().anyMatch(item -> !Objects.equals(item.getCartId(), selectedCart.getCartId()))) throw new AppException(CommonMessageUtils.CART_NOT_MATCH);
        if (cartItems.isEmpty())
            throw new AppException(CommonMessageUtils.LIST_CART_ITEMS_EMPTY);

        String status = PaymentMethod.VNPAY.equals(orderRequest.getPaymentMethod())
                ? OrderStatus.CREATED
                : OrderStatus.PENDING;
        // Tạo đơn hàng
        Order order = createOrderFromCartItem(cartItems, profile);
        order.setOrderStatus(status);
        calculateTotalAmount(order, orderRequest.getShippingFee(), orderRequest.getCouponCode(), profile);
        Order saveOrder = orderRepository.save(order);

        // set address shipping from address profile
        setShippingOrder(saveOrder, orderRequest.getAddressId(), orderRequest.getProfileId(), orderRequest.getOrderNote());


        Payment payment = paymentService.createPayment(
                order,
                orderRequest.getPaymentMethod(),
                order.getTotalAmount().longValue(),
                orderRequest.getBankCode()
        );

        order.setPaymentId(payment.getPaymentId());

        orderRepository.save(order);

        historyService.logStatusChange(order, status, null);

        // xoa item sau khi dat
        cartService.removeSelectedItemsFromCartByItemIds(
                models.cartForProfile(order.getProfileId()).getCartId(),
                orderRequest.getListItemId()
        );

        return orderMapper.toResponse(saveOrder);
    }

    // placeOrder Buy from Cart -> create OrderItem -> Order -> Payment
    // =================== PLACE ORDER FROM CART ======================
    @Override
    @Transactional
    public OrderResponse placeOrder(PlaceOrderFromCartRequest orderRequest) {
        log.info("================= START PLACE ORDER ===================");

        // Đặt hàng
        Cart cart = cartService.getCartByUserProfileId(orderRequest.getProfileId());
        models.getCarts().findByIdForUpdate(cart.getCartId()).orElseThrow(() -> new AppException(CommonMessageUtils.CART_NOT_EXISTED));
        if (models.cartItems(cart.getCartId()).isEmpty()) throw new AppException(CommonMessageUtils.CART_NOT_EXISTED);

        String status = PaymentMethod.VNPAY.equals(orderRequest.getPaymentMethod())
                ? OrderStatus.CREATED
                : OrderStatus.PENDING;

        // Tạo đơn hàng
        Order order = createOrderFromCart(cart);
        order.setOrderStatus(status);

        calculateTotalAmount(order, orderRequest.getShippingFee(), orderRequest.getCouponCode(), models.profile(cart.getProfileId()));
        Order saveOrder = orderRepository.save(order);

        // set address shipping from address profile
        setShippingOrder(saveOrder, orderRequest.getAddressId(), orderRequest.getProfileId(), orderRequest.getOrderNote());


        Payment payment = paymentService.createPayment(
                order,
                orderRequest.getPaymentMethod(),
                order.getTotalAmount().longValue(),
                orderRequest.getBankCode()
        );

        order.setPaymentId(payment.getPaymentId());

        orderRepository.save(order);

        historyService.logStatusChange(order, status, null);

        // clear cart
        cartService.removeSelectedItemsFromCartByItemIds(cart.getCartId(), models.cartItems(cart.getCartId()).stream().map(CartItem::getCartItemId).toList());

        return orderMapper.toResponse(saveOrder);
    }

    // test
    @Transactional
    public VnpayResponse finalizeVnPayCallback(HttpServletRequest request) {

        VnpayResponse resp = paymentService.handleVnPayCallback(request);

        String txnRef = request.getParameter("vnp_TxnRef");
        if (txnRef == null) {
            return resp;
        }

        Long orderId = Long.parseLong(txnRef);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(CommonMessageUtils.ORDER_NOT_EXISTED));

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new AppException(CommonMessageUtils.PAYMENT_NOT_EXISTED));

        // ========== PAYMENT SUCCESS ==========
        if (PaymentStatus.PAID.equals(payment.getPaymentStatus())) {
            order.setOrderStatus(OrderStatus.PENDING);
        }

        // ===== PAYMENT FAILED → CHO PHÉP RETRY =====
        if (PaymentStatus.FAILED.equals(payment.getPaymentStatus())) {
            if (!OrderStatus.CREATED.equals(order.getOrderStatus())) {
                order.setOrderStatus(OrderStatus.CREATED);
                historyService.logStatusChange(order, OrderStatus.CREATED, null);
            }
        }

        orderRepository.save(order);
        return resp;
    }


    // ================== CONFIRM COD ============================
    @Override
    @AdminOnly
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse confirmCashOrder(Long orderId, Principal principal) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(CommonMessageUtils.ORDER_NOT_EXISTED));

        if (OrderStatus.DELIVERED.equals(order.getOrderStatus()))
            throw new AppException(CommonMessageUtils.ORDER_ALREADY_DELIVERED);

        User admin = userService.findUserByPrincipal(principal);

        if (models.orderItems(order.getOrderId()).isEmpty()) throw new AppException(CommonMessageUtils.ORDER_ITEM_EMPTY);

        for (OrderItem item : models.orderItems(order.getOrderId())) {
            inventoryService.confirmOrder(
                    item.getProductId(),
                    item.getQuantity()
            );
        }

        order.setOrderStatus(OrderStatus.DELIVERED);
        paymentService.confirmCodPayment(order);
        historyService.logStatusChange(order, OrderStatus.DELIVERED, admin);
        calculateSoldCount(order);

        return orderMapper.toResponse(order);
    }

    @Override
    @AdminOnly
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse confirmVnpayOrder(Long orderId, Principal principal) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(CommonMessageUtils.ORDER_NOT_EXISTED));

        if (OrderStatus.DELIVERED.equals(order.getOrderStatus()))
            throw new AppException(CommonMessageUtils.ORDER_ALREADY_DELIVERED);

        Payment payment = Optional.ofNullable(models.payment(order.getPaymentId()))
                .orElseThrow(() -> new AppException(CommonMessageUtils.PAYMENT_NOT_EXISTED));

        if (!PaymentMethod.VNPAY.equals(payment.getPaymentMethod()))
            throw new AppException(CommonMessageUtils.INVALID_PAYMENT_METHOD);

        if (!PaymentStatus.PAID.equals(payment.getPaymentStatus()))
            throw new AppException(CommonMessageUtils.PAYMENT_NOT_PAID);


        if (models.orderItems(order.getOrderId()).isEmpty()) throw new AppException(CommonMessageUtils.ORDER_ITEM_EMPTY);

        for (OrderItem item : models.orderItems(order.getOrderId())) {
            inventoryService.confirmOrder(
                    item.getProductId(),
                    item.getQuantity()
            );
        }

        User admin = userService.findUserByPrincipal(principal);

        order.setOrderStatus(OrderStatus.DELIVERED);
        historyService.logStatusChange(order, OrderStatus.DELIVERED, admin);

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        // Tìm kiếm đơn hàng
        Order order = orderRepository.findByIdForUpdate(orderId).orElseThrow(() -> new AppException(CommonMessageUtils.ORDER_NOT_EXISTED));

        if (OrderStatus.CANCELLED.equals(order.getOrderStatus())) {
            throw new AppException(CommonMessageUtils.ORDER_ALREADY_CANCELLED);
        }

        if (OrderStatus.DELIVERED.equals(order.getOrderStatus())) {
            throw new AppException(CommonMessageUtils.ORDER_CANCEL_FAILED);
        }

        // Order was paid/confirm restock
        if (OrderStatus.SHIPPED.equals(order.getOrderStatus()) || OrderStatus.PENDING.equals(order.getOrderStatus())) {
            models.orderItems(order.getOrderId()).forEach(orderItem -> {
                // restock
                inventoryService.restock(orderItem.getProductId(), orderItem.getQuantity());
            });
        }

        // Update status order
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    // ============= UPDATE STATUS ====================
    @Override
    @Transactional
    public Order updateOrderStatus(Long orderId, String newStatus, User admin) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(CommonMessageUtils.ORDER_NOT_EXISTED));

        if (newStatus == null || !OrderStatus.VALUES.contains(newStatus)) throw new AppException(CommonMessageUtils.VALIDATION_ERROR);
        String currentStatus = order.getOrderStatus();

        // NGĂN CHẶN CHUYỂN TRẠNG THÁI SAI LUẬT
        if (OrderStatus.DELIVERED.equals(currentStatus)) {
            throw new AppException(CommonMessageUtils.ORDER_STATUS_FINAL);
        }

        if (OrderStatus.CANCELLED.equals(currentStatus)) {
            throw new AppException(CommonMessageUtils.ORDER_ALREADY_CANCELLED);
        }
        historyService.logStatusChange(order, newStatus, admin);
        orderRepository.updateStatus(order.getOrderId(), newStatus);
        return order;
    }

    @Override
    @Transactional
    public void deleteOrder(Long orderId) {
        log.info("deleteOrder().........");
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(CommonMessageUtils.ORDER_NOT_EXISTED));

        if (OrderStatus.DELIVERED.equals(order.getOrderStatus()) || OrderStatus.SHIPPED.equals(order.getOrderStatus())) {
            throw new AppException(CommonMessageUtils.ORDER_CANCEL_FAILED);
        }
        models.getOrderItems().deleteByOrderId(orderId);
        models.getDeliveryAddresses().deleteByOrderId(orderId);
        models.getHistories().deleteByOrderId(orderId);
        orderRepository.delete(order);
    }

    // ==================== UPDATE SHIPPING ORDER ==========
    @Override
    public Order updateShippingOrder(Long orderId, Long addressId, String orderNote) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(CommonMessageUtils.ORDER_NOT_EXISTED));
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new AppException(CommonMessageUtils.ADDRESS_NOT_EXISTED));

        if (!Objects.equals(order.getProfileId(), address.getProfileId()))
            throw new AppException(CommonMessageUtils.ORDER_ADDRESS_MISMATCH);

        if (!OrderStatus.PENDING.equals(order.getOrderStatus()))
            throw new AppException(CommonMessageUtils.ORDER_CANNOT_CHANGE_ADDRESS);

        deliveryAddressService.updateDeliveryAddressFromAddressId(address.getAddressId(), order, orderNote);
        return orderRepository.save(order);
    }

    // =========== CREATE ORDER FROM CART =============
    private Order createOrderFromCart(Cart cart) {
        Order order = Order.builder()
                .profileId(cart.getProfileId())
                .userId(cart.getUserId())
                .orderDate(LocalDate.now())
                .orderStatus(OrderStatus.PENDING)
                .build();

        orderRepository.save(order);
        List<OrderItem> orderItems = models.cartItems(cart.getCartId()).stream()
                .map(ci ->
                        OrderItem.builder()
                                .orderId(order.getOrderId())
                                .productId(ci.getProductId())
                                .variantId(ci.getVariantId()).
                                quantity(ci.getQuantity())
                                .price(ci.getUnitPrice())
                                .cartItemId(ci.getCartItemId())
                                .build()
                )
                .collect(Collectors.toList());

        orderItems.forEach(item -> {
            Product product = models.product(item.getProductId());
            item.setProductTitle(product.getProductName());
            item.setPrice(product.getSpecialPrice() != null ? product.getSpecialPrice() : product.getPrice());
        });
        models.getOrderItems().saveAll(orderItems);
        return order;
    }

    // ========== CREATE ORDER FROM CART ITEMS =======
    private Order createOrderFromCartItem(List<CartItem> cartItems, UserProfile profile) {
        Order order = Order.builder()
                .profileId(profile.getProfileId())
                .orderDate(LocalDate.now())
                .orderStatus(OrderStatus.PENDING)
                .build();

        order.setUserId(profile.getUserId());
        orderRepository.save(order);
        List<OrderItem> orderItems = cartItems.stream()
                .map(item ->
                        OrderItem.builder()
                                .orderId(order.getOrderId())
                                .quantity(item.getQuantity())
                                .productId(item.getProductId())
                                .variantId(item.getVariantId())
                                .price(item.getUnitPrice())
                                .cartItemId(item.getCartItemId())
                                .build()
                ).toList();

        orderItems.forEach(item -> {
            Product product = models.product(item.getProductId());
            item.setProductTitle(product.getProductName());
            item.setPrice(product.getSpecialPrice() != null ? product.getSpecialPrice() : product.getPrice());
        });
        models.getOrderItems().saveAll(orderItems);
        return order;
    }

    // ============== UPDATE CALCULATE SOLD COUNT ==============
    private void calculateSoldCount(Order order) {
        models.orderItems(order.getOrderId())
                .forEach(item -> {
                    Product product = models.product(item.getProductId());
                    int quantity = item.getQuantity();
                    product.setSoldCount(product.getSoldCount() + quantity);
                    productRepository.save(product);
                });
    }

    // ============== CALCULATE TOTAL AMOUNT ==================
    private void calculateTotalAmount(Order order, BigDecimal shippingFee, String couponCode, UserProfile profile) {
        // total amount from order
        BigDecimal itemTotalAmount = models.orderItems(order.getOrderId()).stream()
                .map(oi -> oi.getPrice().multiply(BigDecimal.valueOf(oi.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(itemTotalAmount);

        // shipping fee
        order.setShippingFee(shippingFee != null && shippingFee.compareTo(BigDecimal.ZERO) >= 0 ? shippingFee : BigDecimal.ZERO);
        order.setDiscountAmount(BigDecimal.ZERO);

        // add shipping fee
        order.setTotalAmount(order.getTotalAmount().add(order.getShippingFee()));

        //  apply coupon
        if (couponCode != null && !couponCode.isBlank()) {
            try {
                BigDecimal discount = couponService.applyCouponToOrder(
                        couponCode,
                        order.getTotalAmount(), // + ship
                        order,
                        profile
                );

                order.setDiscountAmount(discount);
                order.setTotalAmount(order.getTotalAmount().subtract(discount));
                if (order.getTotalAmount().compareTo(BigDecimal.ZERO) < 0) order.setTotalAmount(BigDecimal.ZERO);
            } catch (AppException e) {
                log.warn("Coupon invalid or expired: {}", couponCode);
            }
        }
    }

    // ============== SET ADDRESS SHIPPING ORDER ========
    private void setShippingOrder(Order order, Long addressId, Long profileId, String orderNote) {
        // set address shipping from address profile
        OrderDeliveryAddress deliveryAddress = deliveryAddressService.createDeliveryAddressFromAddressId(addressId, profileId, orderNote);
        deliveryAddress.setOrderId(order.getOrderId());
        deliveryAddress.setCreatedAt(LocalDateTime.now());
        models.getDeliveryAddresses().save(deliveryAddress);
    }

}
