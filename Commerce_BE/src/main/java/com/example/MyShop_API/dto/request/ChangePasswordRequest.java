package com.example.MyShop_API.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "{validation.password.is.required}")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "{validation.password.must.contain.at.least.8.characters.including.uppercase.lowercase.digits.and.special.characters}"
    )
    String oldPassword;

    @NotBlank(message = "{validation.password.is.required}")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "{validation.password.must.contain.at.least.8.characters.including.uppercase.lowercase.digits.and.special.characters}"
    )
    String newPassword;

    @NotBlank(message = "{validation.password.is.required}")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "{validation.password.must.contain.at.least.8.characters.including.uppercase.lowercase.digits.and.special.characters}"
    )
    String confirmPassword;
}
