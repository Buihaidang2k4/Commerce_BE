package com.example.MyShop_API.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequest {
    @NotBlank @Size(max = 100)
    private String roleName;
    @Size(max = 255)
    private String name;
    @Size(max = 4000)
    private String description;
    @Size(max = 1000)
    private Set<@Positive Long> permissionIds;
}
