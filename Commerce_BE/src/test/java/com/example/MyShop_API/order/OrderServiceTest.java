package com.example.MyShop_API.order;

import com.example.MyShop_API.Enum.OrderStatus;
import com.example.MyShop_API.entity.Order;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.constants.CommonMessageUtils;
import com.example.MyShop_API.repo.OrderRepository;
import com.example.MyShop_API.service.cart.ICartService;
import com.example.MyShop_API.service.inventory.IInventoryService;
import com.example.MyShop_API.service.order.OrderService;
import com.example.MyShop_API.service.payment.IPaymentService;
import com.example.MyShop_API.testdata.OrderTestData;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

import java.util.Optional;

import static org.mockito.Mockito.when;


@Slf4j
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    OrderService orderService;

    @Mock
    OrderRepository orderRepository;

    @Mock
    IInventoryService inventoryService;

    @Mock
    ICartService cartService;

    @Mock
    IPaymentService iPaymentService;


    @Test
    void cancelOrder_pending_shouldCancelAndReleaseInventory() {
        log.info("OrderServiceTest::cancelOrder_pending_shouldCancelAndReleaseInventory");
        Order order = OrderTestData.pendingOrder();

        when(orderRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(order));

        orderService.cancelOrder(1L);

        Assertions.assertThat(order.getOrderStatus())
                .isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void cancelOrder_delivered_shouldCancelAndReleaseInventory() {
        Order order = OrderTestData.deliveredOrder();

        when(orderRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(order));

        Assertions.assertThatThrownBy(() -> orderService.cancelOrder(1L))
                .isInstanceOfSatisfying(AppException.class, e -> Assertions.assertThat(e.getMessageKey()).isEqualTo(CommonMessageUtils.ORDER_CANCEL_FAILED));

        verify(inventoryService, never()).restock(anyLong(), anyInt());
    }

    @Test
    void cancelOrder_alreadyCanceled_shouldThrowException() {
        Order order = OrderTestData.cancelOrder();

        when(orderRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(order));

        Assertions.assertThatThrownBy(() -> orderService.cancelOrder(1L))
                .isInstanceOfSatisfying(AppException.class, e -> Assertions.assertThat(e.getMessageKey()).isEqualTo(CommonMessageUtils.ORDER_ALREADY_CANCELLED));
    }


}
