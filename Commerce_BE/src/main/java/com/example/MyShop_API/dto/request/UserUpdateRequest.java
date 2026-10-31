package com.example.MyShop_API.dto.request;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

@Data // Auto create getter setter Object toString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    @NotEmpty @Size(max = 100)
    List<@NotBlank String> roles;
}
