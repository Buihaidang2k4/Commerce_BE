package com.example.MyShop_API.dto.request;

import com.example.MyShop_API.constants.AuthConstants;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResetPasswordRequest {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Pattern(regexp = AuthConstants.OTP_PATTERN)
    private String otp;
    @Size(min = AuthConstants.PASSWORD_MIN_LENGTH, max = AuthConstants.PASSWORD_MAX_LENGTH)
    @NotBlank(message = "{validation.password.is.required}")
    @Pattern(regexp = AuthConstants.PASSWORD_PATTERN, message = "{validation.password.must.contain.at.least.8.characters.including.uppercase.lowercase.digits.and.special.characters}")
    private String newPassword;
}
