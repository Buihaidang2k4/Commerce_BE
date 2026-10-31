package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.request.PaymentRequest;
import com.example.MyShop_API.dto.response.PaymentDto;
import com.example.MyShop_API.dto.response.PaymentResponse;
import com.example.MyShop_API.entity.PaymentIntent;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PaymentIntentMapper {
    @Mapping(source = "paymentMethod", target = "methodCode")
    PaymentIntent toEntity(PaymentRequest paymentRequest);

    @Mapping(source = "id", target = "paymentId")
    @Mapping(source = "methodCode", target = "paymentMethod")
    PaymentResponse toResponse(PaymentIntent payment);




    @Mapping(source = "id", target = "paymentId")
    @Mapping(source = "methodCode", target = "paymentMethod")
    @Mapping(source = "status", target = "paymentStatus")
    @Mapping(source = "providerStatus", target = "status")
    @Mapping(source = "providerTransactionId", target = "transactionNo")
    PaymentDto toDto(PaymentIntent payment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "paymentMethod", target = "methodCode")
    void update(PaymentRequest paymentRequest, @MappingTarget PaymentIntent payment);
}
