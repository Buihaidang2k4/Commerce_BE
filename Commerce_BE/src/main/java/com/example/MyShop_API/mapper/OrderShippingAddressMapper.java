package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.request.AddressRequest;
import com.example.MyShop_API.dto.request.AddressUpdateRequest;
import com.example.MyShop_API.dto.response.AddressResponse;
import com.example.MyShop_API.dto.response.OrderDeliveryAddressResponse;
import com.example.MyShop_API.entity.UserAddress;
import com.example.MyShop_API.entity.OrderShippingAddress;
import org.mapstruct.*;
import org.springframework.util.StringUtils;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderShippingAddressMapper {
    @Mapping(source = "deliveryInstructions", target = "deliveryNote")
    OrderDeliveryAddressResponse toResponse(OrderShippingAddress address);
}
