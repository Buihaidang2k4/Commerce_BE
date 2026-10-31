package com.example.MyShop_API.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PermissionRequest {
    @NotBlank @Size(max = 100)
    private String code;
    @Size(max = 100)
    private String resourceCode;
    @Size(max = 100)
    private String actionCode;
    @Size(max = 100)
    private String scopeCode;
    @Size(max = 4000)
    private String description;
}
