package com.example.MyShop_API.service.order_delivery_address;

import com.example.MyShop_API.entity.Order;
import com.example.MyShop_API.entity.OrderDeliveryAddress;

public interface IOrderDeliveryAddressService {
    OrderDeliveryAddress getOrderDeliveryAddress(Long orderId);

    OrderDeliveryAddress createDeliveryAddressFromAddressId(Long addressId, Long profileId, String extraDeliveryNote);

    OrderDeliveryAddress updateDeliveryAddressFromAddressId(Long addressId, Order order, String extraDeliveryNote);

}
