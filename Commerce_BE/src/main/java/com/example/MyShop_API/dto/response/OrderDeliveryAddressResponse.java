package com.example.MyShop_API.dto.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDeliveryAddressResponse {
    String recipientName;
    String recipientPhone;
    String street;           // Số nhà, tên đường
    String ward;             // Phường/Xã
    String district;         // Quận/Huyện
    String province;         // Tỉnh/Thành phố
    String postalCode;       // Có thể null ở VN
    String deliveryNote;     // Ghi chú: "Giao sau 17h", "Để trước cửa"...
}
