package com.example.MyShop_API.dto.request;


import com.example.MyShop_API.constants.PaymentMethodConstants;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlaceOrderFromCartRequest {
    @NotNull(message = "{validation.profile.id.is.required}")
    Long profileId;

    @NotNull(message = "{validation.address.id.is.required}")
    Long addressId;

    @NotNull(message = "{validation.payment.method.is.required}")
    String paymentMethod; // VNPAY : CASH
    String bankCode = "NCB"; // VNPAY

    @NotNull(message = "{validation.shipping.fee.is.required}")
    @Min(value = 0, message = "{validation.shipping.fee.cannot.be.negative}")
    BigDecimal shippingFee;
    String couponCode;
    String orderNote;
}
