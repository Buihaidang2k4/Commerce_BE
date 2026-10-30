package com.example.MyShop_API.testdata;

import com.example.MyShop_API.Enum.OrderStatus;
import com.example.MyShop_API.entity.Order;
import com.example.MyShop_API.entity.OrderItem;
import com.example.MyShop_API.entity.Product;

import java.math.BigDecimal;
import java.util.Set;

public final class OrderTestData {

    private OrderTestData() {
    } // không cho new

    public static Order pendingOrder() {
        Order order = new Order();
        order.setOrderId(1L);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderItems(Set.of(orderItem(order)));
        return order;
    }

    public static Order deliveredOrder() {
        Order order = new Order();
        order.setOrderId(2L);
        order.setOrderStatus(OrderStatus.DELIVERED);
        return order;
    }

    public static Order cancelOrder() {
        Order order = new Order();
        order.setOrderId(2L);
        order.setOrderStatus(OrderStatus.CANCELLED);
        return order;
    }

    private static OrderItem orderItem(Order order) {
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setQuantity(2);
        item.setPrice(BigDecimal.valueOf(100_000));
        item.setProduct(product());
        return item;
    }

    private static Product product() {
        Product product = new Product();
        product.setProductId(10L);
        product.setPrice(BigDecimal.valueOf(100_000));
        return product;
    }
}
