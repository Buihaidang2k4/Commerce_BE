package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.request.PaymentRequest;
import com.example.MyShop_API.dto.response.PaymentDto;
import com.example.MyShop_API.dto.response.PaymentResponse;
import com.example.MyShop_API.entity.Payment;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    Payment toEntity(PaymentRequest paymentRequest);

    PaymentResponse toResponse(Payment payment);

    @Mapping(source = "order.orderId", target = "orderId")
    PaymentDto toDto(Payment payment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(PaymentRequest paymentRequest, @MappingTarget Payment payment);
}
