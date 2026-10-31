package com.example.MyShop_API.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data // Auto create getter setter Object toString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileRequest {
    @NotNull(message = "{validation.username.cannot.be.null}")
    @Size(min = 5, max = 30, message = "{validation.username.must.be.between.5.and.30.characters.long}")
    String username;

    @NotNull(message = "{validation.mobile.number.cannot.be.null}")
    @Size(min = 10, max = 10, message = "{validation.mobile.number.must.be.exactly.10.digits.long}")
    @Pattern(regexp = "^\\d{10}$", message = "{validation.mobile.number.must.contain.only.numbers}")
    String mobileNumber;

    @NotNull(message = "{validation.gender.cannot.be.null}")
    Boolean gender;

    @NotNull(message = "{validation.birth.date.cannot.be.null}")
    @Past(message = "{validation.birth.date.must.be.in.the.past}")
    LocalDate birthDate;
}
