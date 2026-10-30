package com.example.MyShop_API.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressResponse {
    Long addressId;
    String fullName;          // Tên người nhận
    String phone;             // SĐT giao hàng
    String street;            // Số nhà, tên đường
    Integer wardCode;
    String ward;              // Phường/Xã
    Integer districtID;
    String district;          // Quận/Huyện
    Integer provinceID;
    String province;          // Tỉnh/Thành phố
    String postalCode;        // Mã bưu điện (có thể null)
    String additionalInfo;
    Boolean isDefault;        // Có phải địa chỉ mặc định không
    String type;              // "HOME", "WORK", "OTHER" → frontend hiển thị icon
    String label;             // Tên do người dùng tự đặt: "Nhà riêng", "Công ty", "Nhà bố mẹ"
    String shortAddress;      // Ví dụ: "123 Đường Láng, Đống Đa, Hà Nội"
    String fullAddress;       // Ví dụ: "Nguyễn Văn A, 0901234567, 123 Đường Láng, P. Láng Thượng, Q. Đống Đa, Hà Nội"
    String createdAt;         // ISO string: "2025-04-05T10:30:00"
    String updatedAt;
}