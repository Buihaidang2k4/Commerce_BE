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
import com.example.MyShop_API.service.cart.IShoppingCartService;
import com.example.MyShop_API.service.coupon.ICouponService;
import com.example.MyShop_API.service.inventory.IStockBalanceService;
import com.example.MyShop_API.service.order_delivery_address.IOrderShippingAddressService;
import com.example.MyShop_API.service.order_status_history.IOrderStatusTransitionService;
import com.example.MyShop_API.service.payment.IPaymentIntentService;
import com.example.MyShop_API.service.user.IUserAccountService;
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
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService implements IOrderService {
    private final com.example.MyShop_API.service.model.ModelLookup models;
    OrderRepository orderRepository;
    IShoppingCartService cartService;
    ProductRepository productRepository;
    IStockBalanceService inventoryService;
    IPaymentIntentService paymentService;
    UserProfileRepository profileRepository;
    ICouponService couponService;
    IOrderStatusTransitionService historyService;
    PaymentIntentRepository paymentRepository;
    IOrderShippingAddressService deliveryAddressService;
    UserAddressRepository addressRepository;
    OrderMapper orderMapper;
    IUserAccountService userService;
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
        return orderRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> getOrdersByStatus(String status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getUserOrders(Long profileId) {
        return orderRepository.findByUserProfileId(profileId);
    }

    // placeOrder Buy Now -> create orderItem -> PaymentIntent
    // ===================== Buy Now =============================
    @Override
    @Transactional
    public OrderResponse buyNow(OrderRequest orderRequest) {
        Product product = productRepository.findById(orderRequest.getProductId())
                .orElseThrow(() -> new AppException(CommonMessageUtils.PRODUCT_NOT_EXISTED));

        UserProfile profile = profileRepository.findById(orderRequest.getProfileId())
                .orElseThrow(() -> new AppException(CommonMessageUtils.PROFILE_NOT_EXISTED));

        if (!inventoryService.reserveStock(product.getId(), orderRequest.getQuantity())) {
            throw new AppException(CommonMessageUtils.INVENTORY_NOT_ENOUGH, models.inventory(product.getId()).getAvailableQuantity(), orderRequest.getQuantity());
        }

        String status = PaymentMethod.VNPAY.equals(orderRequest.getPaymentMethod())
                ? OrderStatus.CREATED
                : OrderStatus.PENDING;

        Order order = Order.builder()
                .userProfileId(profile.getId())
                .orderDate(LocalDate.now())
                .status(status)
                .build();

        OrderItem item = OrderItem.builder()
                .productId(product.getId())
                .quantity(orderRequest.getQuantity())
                .unitPrice(product.getSpecialPrice() != null
                        ? product.getSpecialPrice()
                        : product.getPrice())
                .build();


        order.setUserId(profile.getUserId());
        orderRepository.save(order);
        item.setOrderId(order.getId());
        item.setProductNameSnapshot(product.getName());
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
        PaymentIntent payment = paymentService.createPayment(
                order,
                orderRequest.getPaymentMethod(),
                order.getTotalAmount().longValue(),
                orderRequest.getBankCode()
        );

        order.setPaymentId(payment.getId());

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

        ShoppingCart selectedCart = models.cartForProfile(profile.getId());
        if (selectedCart == null || cartItems.size() != orderRequest.getListItemId().stream().distinct().count() || cartItems.stream().anyMatch(item -> !Objects.equals(item.getCartId(), selectedCart.getId()))) throw new AppException(CommonMessageUtils.CART_NOT_MATCH);
        if (cartItems.isEmpty())
            throw new AppException(CommonMessageUtils.LIST_CART_ITEMS_EMPTY);

        String status = PaymentMethod.VNPAY.equals(orderRequest.getPaymentMethod())
                ? OrderStatus.CREATED
                : OrderStatus.PENDING;
        // Tạo đơn hàng
        Order order = createOrderFromCartItem(cartItems, profile);
        order.setStatus(status);
        calculateTotalAmount(order, orderRequest.getShippingFee(), orderRequest.getCouponCode(), profile);
        Order saveOrder = orderRepository.save(order);

        // set address shipping from address profile
        setShippingOrder(saveOrder, orderRequest.getAddressId(), orderRequest.getProfileId(), orderRequest.getOrderNote());


        PaymentIntent payment = paymentService.createPayment(
                order,
                orderRequest.getPaymentMethod(),
                order.getTotalAmount().longValue(),
                orderRequest.getBankCode()
        );

        order.setPaymentId(payment.getId());

        orderRepository.save(order);

        historyService.logStatusChange(order, status, null);

        // xoa item sau khi dat
        cartService.removeSelectedItemsFromCartByItemIds(
                models.cartForProfile(order.getUserProfileId()).getId(),
                orderRequest.getListItemId()
        );

        return orderMapper.toResponse(saveOrder);
    }

    // placeOrder Buy from ShoppingCart -> create OrderItem -> Order -> PaymentIntent
    // =================== PLACE ORDER FROM CART ======================
    @Override
    @Transactional
    public OrderResponse placeOrder(PlaceOrderFromCartRequest orderRequest) {
        log.info("================= START PLACE ORDER ===================");

        // Đặt hàng
        ShoppingCart cart = cartService.getCartByUserProfileId(orderRequest.getProfileId());
        models.getCarts().findByIdForUpdate(cart.getId()).orElseThrow(() -> new AppException(CommonMessageUtils.CART_NOT_EXISTED));
        if (models.cartItems(cart.getId()).isEmpty()) throw new AppException(CommonMessageUtils.CART_NOT_EXISTED);

        String status = PaymentMethod.VNPAY.equals(orderRequest.getPaymentMethod())
                ? OrderStatus.CREATED
                : OrderStatus.PENDING;

        // Tạo đơn hàng
        Order order = createOrderFromCart(cart);
        order.setStatus(status);

        calculateTotalAmount(order, orderRequest.getShippingFee(), orderRequest.getCouponCode(), models.profile(cart.getUserProfileId()));
        Order saveOrder = orderRepository.save(order);

        // set address shipping from address profile
        setShippingOrder(saveOrder, orderRequest.getAddressId(), orderRequest.getProfileId(), orderRequest.getOrderNote());


        PaymentIntent payment = paymentService.createPayment(
                order,
                orderRequest.getPaymentMethod(),
                order.getTotalAmount().longValue(),
                orderRequest.getBankCode()
        );

        order.setPaymentId(payment.getId());

        orderRepository.save(order);

        historyService.logStatusChange(order, status, null);

        // clear cart
        cartService.removeSelectedItemsFromCartByItemIds(cart.getId(), models.cartItems(cart.getId()).stream().map(CartItem::getId).toList());

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

        PaymentIntent payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new AppException(CommonMessageUtils.PAYMENT_NOT_EXISTED));

        // ========== PAYMENT SUCCESS ==========
        if (PaymentStatus.PAID.equals(payment.getStatus())) {
            order.setStatus(OrderStatus.PENDING);
        }

        // ===== PAYMENT FAILED → CHO PHÉP RETRY =====
        if (PaymentStatus.FAILED.equals(payment.getStatus())) {
            if (!OrderStatus.CREATED.equals(order.getStatus())) {
                order.setStatus(OrderStatus.CREATED);
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

        if (OrderStatus.DELIVERED.equals(order.getStatus()))
            throw new AppException(CommonMessageUtils.ORDER_ALREADY_DELIVERED);

        UserAccount admin = userService.findUserByPrincipal(principal);

        if (models.orderItems(order.getId()).isEmpty()) throw new AppException(CommonMessageUtils.ORDER_ITEM_EMPTY);

        for (OrderItem item : models.orderItems(order.getId())) {
            inventoryService.confirmOrder(
                    item.getProductId(),
                    item.getQuantity()
            );
        }

        order.setStatus(OrderStatus.DELIVERED);
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

        if (OrderStatus.DELIVERED.equals(order.getStatus()))
            throw new AppException(CommonMessageUtils.ORDER_ALREADY_DELIVERED);

        PaymentIntent payment = Optional.ofNullable(models.payment(order.getPaymentId()))
                .orElseThrow(() -> new AppException(CommonMessageUtils.PAYMENT_NOT_EXISTED));

        if (!PaymentMethod.VNPAY.equals(payment.getMethodCode()))
            throw new AppException(CommonMessageUtils.INVALID_PAYMENT_METHOD);

        if (!PaymentStatus.PAID.equals(payment.getStatus()))
            throw new AppException(CommonMessageUtils.PAYMENT_NOT_PAID);


        if (models.orderItems(order.getId()).isEmpty()) throw new AppException(CommonMessageUtils.ORDER_ITEM_EMPTY);

        for (OrderItem item : models.orderItems(order.getId())) {
            inventoryService.confirmOrder(
                    item.getProductId(),
                    item.getQuantity()
            );
        }

        UserAccount admin = userService.findUserByPrincipal(principal);

        order.setStatus(OrderStatus.DELIVERED);
        historyService.logStatusChange(order, OrderStatus.DELIVERED, admin);

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        // Tìm kiếm đơn hàng
        Order order = orderRepository.findByIdForUpdate(orderId).orElseThrow(() -> new AppException(CommonMessageUtils.ORDER_NOT_EXISTED));

        if (OrderStatus.CANCELLED.equals(order.getStatus())) {
            throw new AppException(CommonMessageUtils.ORDER_ALREADY_CANCELLED);
        }

        if (OrderStatus.DELIVERED.equals(order.getStatus())) {
            throw new AppException(CommonMessageUtils.ORDER_CANCEL_FAILED);
        }

        // Order was paid/confirm restock
        if (OrderStatus.SHIPPED.equals(order.getStatus()) || OrderStatus.PENDING.equals(order.getStatus())) {
            models.orderItems(order.getId()).forEach(orderItem -> {
                // restock
                inventoryService.restock(orderItem.getProductId(), orderItem.getQuantity());
            });
        }

        // Update status order
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    // ============= UPDATE STATUS ====================
    @Override
    @Transactional
    public Order updateOrderStatus(Long orderId, String newStatus, UserAccount admin) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(CommonMessageUtils.ORDER_NOT_EXISTED));

        if (newStatus == null || !OrderStatus.VALUES.contains(newStatus)) throw new AppException(CommonMessageUtils.VALIDATION_ERROR);
        String currentStatus = order.getStatus();

        // NGĂN CHẶN CHUYỂN TRẠNG THÁI SAI LUẬT
        if (OrderStatus.DELIVERED.equals(currentStatus)) {
            throw new AppException(CommonMessageUtils.ORDER_STATUS_FINAL);
        }

        if (OrderStatus.CANCELLED.equals(currentStatus)) {
            throw new AppException(CommonMessageUtils.ORDER_ALREADY_CANCELLED);
        }
        historyService.logStatusChange(order, newStatus, admin);
        orderRepository.updateStatus(order.getId(), newStatus);
        return order;
    }

    @Override
    @Transactional
    public void deleteOrder(Long orderId) {
        log.info("deleteOrder().........");
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(CommonMessageUtils.ORDER_NOT_EXISTED));

        if (OrderStatus.DELIVERED.equals(order.getStatus()) || OrderStatus.SHIPPED.equals(order.getStatus())) {
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
        UserAddress address = addressRepository.findById(addressId).orElseThrow(() -> new AppException(CommonMessageUtils.ADDRESS_NOT_EXISTED));

        if (!Objects.equals(order.getUserProfileId(), address.getUserProfileId()))
            throw new AppException(CommonMessageUtils.ORDER_ADDRESS_MISMATCH);

        if (!OrderStatus.PENDING.equals(order.getStatus()))
            throw new AppException(CommonMessageUtils.ORDER_CANNOT_CHANGE_ADDRESS);

        deliveryAddressService.updateDeliveryAddressFromAddressId(address.getId(), order, orderNote);
        return orderRepository.save(order);
    }

    // =========== CREATE ORDER FROM CART =============
    private Order createOrderFromCart(ShoppingCart cart) {
        Order order = Order.builder()
                .userProfileId(cart.getUserProfileId())
                .userId(cart.getUserId())
                .orderDate(LocalDate.now())
                .status(OrderStatus.PENDING)
                .build();

        orderRepository.save(order);
        List<OrderItem> orderItems = models.cartItems(cart.getId()).stream()
                .map(ci ->
                        OrderItem.builder()
                                .orderId(order.getId())
                                .productId(ci.getProductId())
                                .variantId(ci.getVariantId()).
                                quantity(ci.getQuantity())
                                .unitPrice(ci.getUnitPrice())
                                .cartItemId(ci.getId())
                                .build()
                )
                .collect(Collectors.toList());

        orderItems.forEach(item -> {
            Product product = models.product(item.getProductId());
            item.setProductNameSnapshot(product.getName());
            item.setUnitPrice(product.getSpecialPrice() != null ? product.getSpecialPrice() : product.getPrice());
        });
        models.getOrderItems().saveAll(orderItems);
        return order;
    }

    // ========== CREATE ORDER FROM CART ITEMS =======
    private Order createOrderFromCartItem(List<CartItem> cartItems, UserProfile profile) {
        Order order = Order.builder()
                .userProfileId(profile.getId())
                .orderDate(LocalDate.now())
                .status(OrderStatus.PENDING)
                .build();

        order.setUserId(profile.getUserId());
        orderRepository.save(order);
        List<OrderItem> orderItems = cartItems.stream()
                .<OrderItem>map(item ->
                        OrderItem.builder()
                                .orderId(order.getId())
                                .quantity(item.getQuantity())
                                .productId(item.getProductId())
                                .variantId(item.getVariantId())
                                .unitPrice(item.getUnitPrice())
                                .cartItemId(item.getId())
                                .build()
                ).toList();

        orderItems.forEach(item -> {
            Product product = models.product(item.getProductId());
            item.setProductNameSnapshot(product.getName());
            item.setUnitPrice(product.getSpecialPrice() != null ? product.getSpecialPrice() : product.getPrice());
        });
        models.getOrderItems().saveAll(orderItems);
        return order;
    }

    // ============== UPDATE CALCULATE SOLD COUNT ==============
    private void calculateSoldCount(Order order) {
        models.orderItems(order.getId())
                .forEach(item -> {
                    Product product = models.product(item.getProductId());
                    int quantity = item.getQuantity();
                    product.setSoldQuantity(product.getSoldQuantity() + quantity);
                    productRepository.save(product);
                });
    }

    // ============== CALCULATE TOTAL AMOUNT ==================
    private void calculateTotalAmount(Order order, BigDecimal shippingFee, String couponCode, UserProfile profile) {
        // total amount from order
        BigDecimal itemTotalAmount = models.orderItems(order.getId()).stream()
                .map(oi -> oi.getUnitPrice().multiply(BigDecimal.valueOf(oi.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(itemTotalAmount);

        // shipping fee
        order.setShippingAmount(shippingFee != null && shippingFee.compareTo(BigDecimal.ZERO) >= 0 ? shippingFee : BigDecimal.ZERO);
        order.setDiscountAmount(BigDecimal.ZERO);

        // add shipping fee
        order.setTotalAmount(order.getTotalAmount().add(order.getShippingAmount()));

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
        OrderShippingAddress deliveryAddress = deliveryAddressService.createDeliveryAddressFromAddressId(addressId, profileId, orderNote);
        deliveryAddress.setOrderId(order.getId());
        deliveryAddress.setCreatedAt(Instant.now());
        models.getDeliveryAddresses().save(deliveryAddress);
    }

}
