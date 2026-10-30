package com.example.MyShop_API.controller;

import com.example.MyShop_API.constants.ApiPath;


import com.example.MyShop_API.constants.OrderStatus;
import com.example.MyShop_API.dto.request.OrderPlaceListItemRequest;
import com.example.MyShop_API.dto.request.PlaceOrderFromCartRequest;
import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.dto.request.OrderRequest;
import com.example.MyShop_API.dto.response.OrderResponse;
import com.example.MyShop_API.entity.Cart;
import com.example.MyShop_API.entity.Order;
import com.example.MyShop_API.entity.User;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.constants.CommonMessageUtils;
import com.example.MyShop_API.mapper.OrderMapper;
import com.example.MyShop_API.repo.UserRepository;
import com.example.MyShop_API.service.order.IOrderService;
import com.example.MyShop_API.service.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(ApiPath.BASE_ORDER)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {
    IOrderService orderService;
    OrderMapper orderMapper;
    UserRepository userRepository;

    @PostMapping(ApiPath.ORDER_BUY_NOW_TEST)
    @Operation(summary = "Buy now ")
    ResponseEntity<ApiResponse<OrderResponse>> buyNowTest(
            @Valid @RequestBody OrderRequest orderRequest) {
        return ResponseEntity.ok(new ApiResponse<>(200, "buy now orders", orderService.buyNow(orderRequest)));
    }


    @GetMapping(ApiPath.ORDER_ALL)
    ResponseEntity<ApiResponse<List<OrderResponse>>> getOrders() {
        List<OrderResponse> responses = orderMapper.toResponse(orderService.getOrders());
        return ResponseEntity.ok(new ApiResponse<>(200, "Get orders", responses));
    }


    @GetMapping(ApiPath.ORDER_ORDER_ORDER_ID)
    ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable Long orderId) {
        OrderResponse orderResponse = orderMapper.toResponse(orderService.getOrder(orderId));
        return ResponseEntity.ok(new ApiResponse<>(200, "Get order", orderResponse));
    }

    @GetMapping(ApiPath.ORDER_STATUS_PAGE)
    ResponseEntity<ApiResponse<List<OrderResponse>>> getOrderByStatusOrPage(@RequestParam String orderStatus,
                                                                            @RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "10") int size,
                                                                            @RequestParam(defaultValue = "orderId") String sortBy,
                                                                            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Order> orders = orderService.getOrdersByStatus(orderStatus, pageable);
        Page<OrderResponse> orderRes = orders.map(orderMapper::toResponse);

        Map<String, Object> res = new HashMap<>();
        res.put("content", orderRes.getContent()); // data
        res.put("currentPage", orderRes.getNumber());
        res.put("totalItems", orderRes.getTotalElements());
        res.put("totalPages", orderRes.getTotalPages());
        res.put("size", orderRes.getSize()); // number one page
        res.put("sortBy", sortBy);

        return ResponseEntity.ok(new ApiResponse(200, "success", res));
    }

    @GetMapping(ApiPath.ORDER_STATUS)
    ResponseEntity<ApiResponse<List<OrderResponse>>> getOrderByStatus(@RequestParam String orderStatus) {
        List<OrderResponse> responses = orderMapper.toResponse(orderService.getOrdersByStatus(orderStatus));
        return ResponseEntity.ok(
                new ApiResponse<>(200, "Get orders by status", responses)
        );
    }

    @GetMapping(ApiPath.ORDER_PROFILE_PROFILE_ID)
    ResponseEntity<ApiResponse<List<OrderResponse>>> getOrderByUser(@PathVariable Long profileId) {
        List<OrderResponse> responses = orderMapper.toResponse(orderService.getUserOrders(profileId));
        return ResponseEntity.ok(new ApiResponse<>(200, "Get user order", responses));
    }

    // ============= BUY NOW ==============
    @PostMapping(ApiPath.ORDER_BUY_NOW)
    @Operation(summary = "Buy now ")
    ResponseEntity<ApiResponse<Object>> buyNow(
            @Valid @RequestBody OrderRequest orderRequest) {
        return ResponseEntity.ok(new ApiResponse<>(200, "Place order success", orderService.buyNow(orderRequest)));
    }

    //============= PLACE ORDER FROM LIST CART ITEM ===============
    @PostMapping(ApiPath.ORDER_FROM_CART_ITEMS)
    @Operation(summary = "Place order from list cart items")
    ResponseEntity<ApiResponse<Object>> placeOrderFromListItem(
            @Valid @RequestBody OrderPlaceListItemRequest listItemRequest) {
        return ResponseEntity.ok(new ApiResponse<>(200, "Place order from cart by items success", orderService.placeOrderFromListCartItems(listItemRequest)));

    }

    // ============= PLACE ORDER FROM CART  ===============
    @PostMapping(ApiPath.ORDER_PLACE_ORDER)
    @Operation(summary = "Place order from cart")
    ResponseEntity<ApiResponse<Object>> placeOrder(
            @RequestBody PlaceOrderFromCartRequest orderRequest,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(new ApiResponse<>(200, "Place order from cart success", orderService.placeOrder(orderRequest)));
    }

    // ============== CONFIRM PAYMENT CASH =================
    @PutMapping(ApiPath.ORDER_ORDER_ORDER_ID_CONFIRM_CASH_PAYMENT)
    ResponseEntity<ApiResponse<OrderResponse>> confirmCashPayment(@PathVariable Long orderId,
                                                                  Principal principal
    ) {

        return ResponseEntity.ok(new ApiResponse<>(200, "Cash payment confirm ", orderService.confirmCashOrder(orderId, principal)));
    }

    // ============== CONFIRM PAYMENT CASH =================
    @PutMapping(ApiPath.ORDER_ORDER_ORDER_ID_CONFIRM_VNPAY_PAYMENT)
    ResponseEntity<ApiResponse<OrderResponse>> confirmVnpayPayment(@PathVariable Long orderId,
                                                                   Principal principal
    ) {

        return ResponseEntity.ok(new ApiResponse<>(200, "Cash payment confirm ", orderService.confirmVnpayOrder(orderId, principal)));
    }


    // ============== UPDATE SHIPPING =================
    @PutMapping(ApiPath.ORDER_ORDER_ORDER_ID_ADDRESS_ADDRESS_ID_UPDATE_ORDER_ADDRESS)
    ResponseEntity<ApiResponse<OrderResponse>> updateShipping(@PathVariable Long orderId,
                                                              @PathVariable Long addressId,
                                                              @RequestParam String orderNote
    ) {
        Order order = orderService.updateShippingOrder(orderId, addressId, orderNote);
        return ResponseEntity.ok(new ApiResponse<>(200, "Cash payment confirm ", orderMapper.toResponse(order)));
    }

    @PutMapping(ApiPath.ORDER_ORDER_ORDER_ID_CANCEL_ORDER)
    ResponseEntity<ApiResponse<?>> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok(new ApiResponse(200, "Cancel Order", null));
    }

    @PutMapping(ApiPath.ORDER_ORDER_UPDATE_STATUS)
    ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(@RequestParam("orderStatus") String orderStatus
            , @RequestParam("orderId") Long orderId
            , Principal principal

    ) {
        String email = principal.getName();
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(CommonMessageUtils.USER_NOT_EXISTED));

        OrderResponse orderResponse = orderMapper.toResponse(orderService.updateOrderStatus(orderId, orderStatus, admin));
        return ResponseEntity.ok(
                new ApiResponse(200, "Update order status success", orderResponse)
        );
    }

    @DeleteMapping(ApiPath.ORDER_ORDER_ORDER_ID_DELETE)
    ResponseEntity<ApiResponse<?>> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok(new ApiResponse(200, "Delete Order Success", null));
    }
}
