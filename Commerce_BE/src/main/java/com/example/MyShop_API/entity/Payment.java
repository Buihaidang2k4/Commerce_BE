package com.example.MyShop_API.entity;

import com.example.MyShop_API.Enum.PaymentMethod;
import com.example.MyShop_API.Enum.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long paymentId;

    @OneToOne(mappedBy = "payment", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    Order order;

    // Phương thức thanh toán (VD: "VNPAY", "COD", "MOMO", ...)
    @Enumerated(EnumType.STRING)
    PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    PaymentStatus paymentStatus;

    // Mã giao dịch VNPay (vnp_TxnRef)
    @Column(unique = true)
    String vnpTxnRef;

    // Số tiền thanh toán (đơn vị: VNĐ)
    Long amount;

    // Nội dung thanh toán (vnp_OrderInfo)
    String orderInfo;

    // Mã ngân hàng (vnp_BankCode)
    String bankCode;

    // Mã phản hồi từ VNPay (vnp_ResponseCode)
    String responseCode;

    // Trạng thái giao dịch (PENDING, SUCCESS, FAILED)
    String status;

    // Thời gian thanh toán (vnp_PayDate)
    LocalDateTime paymentDate;
    LocalDateTime expiredAt;

    // mã giao dịch VnPay
    String transactionNo;

    // vnp_CardType
    String cardType;
}
