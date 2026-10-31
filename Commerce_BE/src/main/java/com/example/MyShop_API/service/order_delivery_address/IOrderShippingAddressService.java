package com.example.MyShop_API.service.order_delivery_address;

import com.example.MyShop_API.entity.Order;
import com.example.MyShop_API.entity.OrderShippingAddress;

public interface IOrderShippingAddressService {
    OrderShippingAddress getOrderDeliveryAddress(Long orderId);

    OrderShippingAddress createDeliveryAddressFromAddressId(Long addressId, Long profileId, String extraDeliveryNote);

    OrderShippingAddress updateDeliveryAddressFromAddressId(Long addressId, Order order, String extraDeliveryNote);

}
