package com.example.MyShop_API.dto.request;

import com.example.MyShop_API.constants.AuthConstants;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {


    @NotBlank(message = "{validation.email.is.required}")
    @Pattern(
            regexp = AuthConstants.EMAIL_PATTERN,
            message = "{validation.email.is.invalid}"
    )
    String email;

    @NotBlank(message = "{validation.password.is.required}")
    @Pattern(
            regexp = AuthConstants.PASSWORD_PATTERN,
            message = "{validation.password.must.contain.at.least.8.characters.including.uppercase.lowercase.digits.and.special.characters}"
    )
    @Size(max = AuthConstants.PASSWORD_MAX_LENGTH)
    String password;

}
