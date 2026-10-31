package com.example.MyShop_API.dto.request;

import com.example.MyShop_API.constants.AuthConstants;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationRequest {
    @NotBlank(message = "{validation.email.is.required}")
    @Pattern(
            regexp = AuthConstants.EMAIL_PATTERN,
            message = "{validation.email.is.invalid}"
    )
    String email;

    @NotBlank(message = "{validation.password.is.required}")
    @Size(max = AuthConstants.PASSWORD_MAX_LENGTH)
    String password;
}
