package com.example.MyShop_API.service.order_delivery_address;

import com.example.MyShop_API.entity.Address;
import com.example.MyShop_API.entity.Order;
import com.example.MyShop_API.entity.OrderDeliveryAddress;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.constants.CommonMessageUtils;
import com.example.MyShop_API.repo.AddressRepository;
import com.example.MyShop_API.repo.OrderDeliveryAddressRepository;
import com.example.MyShop_API.repo.OrderRepository;
import io.netty.util.internal.StringUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderDeliveryAddressService implements IOrderDeliveryAddressService {
    private final com.example.MyShop_API.service.model.ModelLookup models;
    AddressRepository addressRepository;
    OrderDeliveryAddressRepository orderDeliveryAddressRepository;
    OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public OrderDeliveryAddress getOrderDeliveryAddress(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(CommonMessageUtils.ORDER_NOT_EXISTED));
        return orderDeliveryAddressRepository.findOrderDeliveryAddressByOrderId(order.getOrderId());
    }

    @Override
    public OrderDeliveryAddress createDeliveryAddressFromAddressId(Long addressId, Long profileId, String extraDeliveryNote) {
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new AppException(CommonMessageUtils.ADDRESS_NOT_EXISTED));

        if (!java.util.Objects.equals(address.getProfileId(), profileId)) throw new AppException(CommonMessageUtils.ORDER_ADDRESS_MISMATCH);
        return OrderDeliveryAddress.builder()
                .createdAt(LocalDateTime.now())
                .recipientName(address.getFullName())
                .recipientPhone(address.getPhone())
                .street(address.getStreet())
                .ward(address.getWard())
                .district(address.getDistrict())
                .province(address.getProvince())
                .postalCode(address.getPostalCode())
                .deliveryNote(
                        StringUtils.hasText(extraDeliveryNote)
                                ? extraDeliveryNote
                                : address.getAdditionalInfo()
                )
                .build();
    }

    @Override
    public OrderDeliveryAddress updateDeliveryAddressFromAddressId(Long addressId, Order order, String extraDeliveryNote) {
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new AppException(CommonMessageUtils.ADDRESS_NOT_EXISTED));
        OrderDeliveryAddress deliveryAddress = models.deliveryAddress(order.getOrderId());
        deliveryAddress.setRecipientName(address.getFullName());
        deliveryAddress.setRecipientPhone(address.getPhone());
        deliveryAddress.setStreet(address.getStreet());
        deliveryAddress.setWard(address.getWard());
        deliveryAddress.setDistrict(address.getDistrict());
        deliveryAddress.setProvince(address.getProvince());
        deliveryAddress.setPostalCode(address.getPostalCode());
        deliveryAddress.setDeliveryNote(StringUtils.hasText(extraDeliveryNote)
                ? extraDeliveryNote
                : address.getAdditionalInfo());

        return orderDeliveryAddressRepository.save(deliveryAddress);
    }


}
