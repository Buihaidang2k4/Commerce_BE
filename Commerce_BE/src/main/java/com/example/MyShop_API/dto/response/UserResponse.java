package com.example.MyShop_API.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    Long id;
    String email;
    boolean enabled = true;
    String lockedReason;
    Instant lockedAt;
    Set<RoleResponse> roles;
    UserProfileResponse userProfile;
}
