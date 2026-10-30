package com.example.MyShop_API.service.order;

import com.example.MyShop_API.dto.request.OrderItemRequest;
import com.example.MyShop_API.dto.response.OrderItemResponse;
import com.example.MyShop_API.entity.OrderItem;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.constants.CommonMessageUtils;
import com.example.MyShop_API.mapper.OrderItemMapper;
import com.example.MyShop_API.repo.OrderItemRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderItemService implements IOrderItemService {
    OrderItemRepository orderItemRepository;
    OrderItemMapper orderItemMapper;

    @Transactional(readOnly = true)
    public List<OrderItemResponse> getOrderItem() {
        return orderItemRepository.findAll().stream().map(orderItemMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public OrderItemResponse getOrderItem(Long orderItemId) {
        OrderItem findOrderItem = orderItemRepository.findById(orderItemId).orElseThrow(() ->
                new AppException(CommonMessageUtils.ORDER_ITEM_NOT_EXISTED));

        return orderItemMapper.toResponse(findOrderItem);
    }

    public OrderItemResponse updateOrderItem(Long orderItemId, OrderItemRequest orderItemRequest) {
        OrderItem findOrderItem = orderItemRepository.findById(orderItemId).orElseThrow(() -> new AppException(CommonMessageUtils.ORDER_ITEM_NOT_EXISTED));
        orderItemMapper.updateOrder(orderItemRequest, findOrderItem);

        findOrderItem = orderItemRepository.save(findOrderItem);
        return orderItemMapper.toResponse(findOrderItem);
    }

    public void deleteOrderItem(Long orderItemId) {
        OrderItem findOrderItem = orderItemRepository.findById(orderItemId).orElseThrow(() -> new AppException(CommonMessageUtils.ORDER_ITEM_NOT_EXISTED));
        orderItemRepository.delete(findOrderItem);
    }
}
