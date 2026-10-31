package com.example.MyShop_API.dto.response;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {
    private Long id;
    private String code;
    private String name;
    private String roleName;
    private String description;
    private List<Long> permissionIds;
}
