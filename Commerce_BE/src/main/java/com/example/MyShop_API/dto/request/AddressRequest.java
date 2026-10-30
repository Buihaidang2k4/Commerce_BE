package com.example.MyShop_API.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressRequest {

    @NotBlank(message = "{validation.recipient.name.is.required}")
    @Size(max = 100, message = "{validation.name.must.contain.at.most.characters}")
    String fullName;

    @NotBlank(message = "{validation.mobile.number.cannot.be.null}")
    @Pattern(regexp = "^(0?)(3[2-9]|5[6|8|9]|7[0|6-9]|8[1-9]|9[0-9])[0-9]{7}$",
            message = "{validation.phone.number.is.invalid}")
    String phone;

    @NotBlank(message = "{validation.street.address.is.required}")
    @Size(max = 255)
    String street;

    Integer wardCode;

    @NotBlank(message = "{validation.ward.is.required}")
    String ward;

    Integer districtID;

    @NotBlank(message = "{validation.district.is.required}")
    String district;

    Integer provinceID;

    @NotBlank(message = "{validation.province.or.city.is.required}")
    String province;
    String postalCode;

    @Size(max = 500, message = "{validation.note.must.contain.at.most.characters}")
    String additionalInfo;

    @Builder.Default
    Boolean isDefault = false;

    String type; // "HOME", "WORK", "OTHER"

    @Size(max = 50)
    String label; // Ví dụ: "Nhà riêng", "Công ty", "Nhà bố mẹ", "Quán cà phê"

    LocalDateTime createdAt = LocalDateTime.now();
}