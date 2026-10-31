package com.example.MyShop_API.service.order;

import com.example.MyShop_API.constants.OrderStatus;
import com.example.MyShop_API.dto.request.OrderPlaceListItemRequest;
import com.example.MyShop_API.dto.request.OrderRequest;
import com.example.MyShop_API.dto.request.PlaceOrderFromCartRequest;
import com.example.MyShop_API.dto.response.OrderResponse;
import com.example.MyShop_API.dto.response.VnpayResponse;
import com.example.MyShop_API.entity.ShoppingCart;
import com.example.MyShop_API.entity.CartItem;
import com.example.MyShop_API.entity.Order;
import com.example.MyShop_API.entity.UserAccount;
import com.example.MyShop_API.exception.AppException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.Principal;
import java.util.List;

public interface IOrderService {
    List<Order> getOrders();

    Order getOrder(Long orderId);

    List<Order> getOrdersByStatus(String status);

    Page<Order> getOrdersByStatus(String status, Pageable pageable);


    List<Order> getUserOrders(Long userProfileId);

    OrderResponse buyNow(OrderRequest orderRequest);

    Object placeOrderFromListCartItems(OrderPlaceListItemRequest orderRequest);

    OrderResponse placeOrder(PlaceOrderFromCartRequest orderRequest) throws AppException;

    VnpayResponse finalizeVnPayCallback(HttpServletRequest request);

    OrderResponse confirmCashOrder(Long orderId, Principal principal);

    OrderResponse confirmVnpayOrder(Long orderId, Principal principal);

    void cancelOrder(Long orderId);

    Order updateOrderStatus(Long orderId, String orderStatus, UserAccount admin);

    void deleteOrder(Long orderId);

    Order updateShippingOrder(Long orderId, Long addressId, String orderNote);
}