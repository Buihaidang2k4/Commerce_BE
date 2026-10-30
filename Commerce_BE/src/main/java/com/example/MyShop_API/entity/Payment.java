package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "payments", indexes = {@Index(name = "idx_payments_order_id", columnList = "order_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;
    private Long orderId;
    private String provider;
    private String providerRef;
    private String paymentMethod;
    private String paymentStatus;
    private String vnpTxnRef;
    private Long amount;
    @Builder.Default
    private String currency = "VND";
    private String orderInfo;
    private String bankCode;
    private String responseCode;
    private String status;
    private LocalDateTime paymentDate;
    private LocalDateTime expiredAt;
    private String transactionNo;
    private String cardType;

}
