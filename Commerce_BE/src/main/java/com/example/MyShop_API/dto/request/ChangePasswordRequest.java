package com.example.MyShop_API.dto.request;

import com.example.MyShop_API.constants.AuthConstants;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "{validation.password.is.required}")
    @Size(max = AuthConstants.PASSWORD_MAX_LENGTH)
    String oldPassword;

    @NotBlank(message = "{validation.password.is.required}")
    @Pattern(
            regexp = AuthConstants.PASSWORD_PATTERN,
            message = "{validation.password.must.contain.at.least.8.characters.including.uppercase.lowercase.digits.and.special.characters}"
    )
    @Size(max = AuthConstants.PASSWORD_MAX_LENGTH)
    String newPassword;

    @NotBlank(message = "{validation.password.is.required}")
    @Pattern(
            regexp = AuthConstants.PASSWORD_PATTERN,
            message = "{validation.password.must.contain.at.least.8.characters.including.uppercase.lowercase.digits.and.special.characters}"
    )
    @Size(max = AuthConstants.PASSWORD_MAX_LENGTH)
    String confirmPassword;
}
