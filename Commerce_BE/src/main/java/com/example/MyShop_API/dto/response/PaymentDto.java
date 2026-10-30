package com.example.MyShop_API.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PaymentDto {
    Long paymentId;
    Long orderId;
    String paymentMethod;
    String paymentStatus;
    String vnpTxnRef;
    Long amount;
    String orderInfo;
    String bankCode;
    String responseCode;
    String status;
    LocalDateTime paymentDate;
    String transactionNo;
    String cardType;
}
