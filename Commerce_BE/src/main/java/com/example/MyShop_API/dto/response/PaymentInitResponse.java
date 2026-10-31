package com.example.MyShop_API.dto.response;

import com.example.MyShop_API.constants.PaymentMethodConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentInitResponse {
    private String method;
    private String redirectUrl; // null nếu COD
}
