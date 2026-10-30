package com.example.MyShop_API.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
            regexp = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$",
            message = "{validation.email.is.invalid}"
    )
    String email;

    @NotBlank(message = "{validation.password.is.required}")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "{validation.password.must.contain.at.least.8.characters.including.uppercase.lowercase.digits.and.special.characters}"
    )
    String password;

}
