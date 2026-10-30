package com.example.MyShop_API.dto.request;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data // Auto create getter setter constructor toString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    List<String> roles;
}
