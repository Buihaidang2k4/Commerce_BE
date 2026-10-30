package com.example.MyShop_API.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    Integer id;
    String email;
    boolean enabled = true;
    String lockedReason;
    LocalDateTime lockedAt;
    Set<RoleResponse> roles;
    UserProfileResponse userProfile;
}
