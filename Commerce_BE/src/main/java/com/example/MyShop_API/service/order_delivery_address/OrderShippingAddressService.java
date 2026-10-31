package com.example.MyShop_API.service.order_delivery_address;

import java.util.Optional;

import com.example.MyShop_API.entity.UserAddress;
import com.example.MyShop_API.entity.Order;
import com.example.MyShop_API.entity.OrderShippingAddress;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.constants.CommonMessageConstants;
import com.example.MyShop_API.repo.UserAddressRepository;
import com.example.MyShop_API.repo.OrderShippingAddressRepository;
import com.example.MyShop_API.repo.OrderRepository;
import io.netty.util.internal.StringUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderShippingAddressService implements IOrderShippingAddressService {
    UserAddressRepository addressRepository;
    OrderShippingAddressRepository orderDeliveryAddressRepository;
    OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public OrderShippingAddress getOrderDeliveryAddress(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(CommonMessageConstants.ORDER_NOT_EXISTED));
        return orderDeliveryAddressRepository.findOrderDeliveryAddressByOrderId(order.getId());
    }

    @Override
    public OrderShippingAddress createDeliveryAddressFromAddressId(Long addressId, Long profileId, String extraDeliveryNote) {
        UserAddress address = addressRepository.findById(addressId).orElseThrow(() -> new AppException(CommonMessageConstants.ADDRESS_NOT_EXISTED));

        if (!java.util.Objects.equals(address.getUserProfileId(), profileId)) throw new AppException(CommonMessageConstants.ORDER_ADDRESS_MISMATCH);
        return OrderShippingAddress.builder()
                .createdAt(Instant.now())
                .recipientName(address.getRecipientName())
                .recipientPhone(address.getRecipientPhone())
                .street(address.getStreet())
                .ward(address.getWard())
                .district(address.getDistrict())
                .province(address.getProvince())
                .postalCode(address.getPostalCode())
                .deliveryInstructions(
                        StringUtils.hasText(extraDeliveryNote)
                                ? extraDeliveryNote
                                : address.getDeliveryInstructions()
                )
                .build();
    }

    @Override
    public OrderShippingAddress updateDeliveryAddressFromAddressId(Long addressId, Order order, String extraDeliveryNote) {
        UserAddress address = addressRepository.findById(addressId).orElseThrow(() -> new AppException(CommonMessageConstants.ADDRESS_NOT_EXISTED));
        OrderShippingAddress deliveryAddress = Optional.ofNullable(order.getId()).map(orderDeliveryAddressRepository::findOrderDeliveryAddressByOrderId).orElse(null);
        deliveryAddress.setRecipientName(address.getRecipientName());
        deliveryAddress.setRecipientPhone(address.getRecipientPhone());
        deliveryAddress.setStreet(address.getStreet());
        deliveryAddress.setWard(address.getWard());
        deliveryAddress.setDistrict(address.getDistrict());
        deliveryAddress.setProvince(address.getProvince());
        deliveryAddress.setPostalCode(address.getPostalCode());
        deliveryAddress.setDeliveryInstructions(StringUtils.hasText(extraDeliveryNote)
                ? extraDeliveryNote
                : address.getDeliveryInstructions());

        return orderDeliveryAddressRepository.save(deliveryAddress);
    }


}
