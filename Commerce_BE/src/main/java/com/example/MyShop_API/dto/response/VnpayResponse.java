package com.example.MyShop_API.dto.response;

import com.example.MyShop_API.Enum.PaymentMethod;
import com.example.MyShop_API.Enum.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class VnpayResponse {
    public String code;
    public String message;
    public String orderCode;
    String orderInfo;
    PaymentStatus paymentStatus;
    PaymentMethod paymentMethod;
}
