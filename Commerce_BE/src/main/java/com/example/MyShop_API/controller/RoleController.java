package com.example.MyShop_API.controller;

import com.example.MyShop_API.constants.ApiPath;

import com.example.MyShop_API.dto.request.RoleRequest;
import com.example.MyShop_API.dto.response.RoleResponse;
import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.service.role.IRoleService;
import com.example.MyShop_API.service.role.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPath.BASE_ROLE)
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RoleController {
    IRoleService roleService;

    @GetMapping(ApiPath.ROLE_ALL)
    ApiResponse<List<RoleResponse>> getRoles() {
        return ApiResponse.<List<RoleResponse>>builder()
                .data(roleService.getAllRoles())
                .build();
    }

    @GetMapping(ApiPath.ROLE_ID)
    ApiResponse<RoleResponse> getRole(@PathVariable String id) {
        return ApiResponse.<RoleResponse>builder()
                .data(roleService.getRoleById(id))
                .build();
    }

    @PostMapping(ApiPath.ROLE_ADD)
    ApiResponse<RoleResponse> createRole(@RequestBody @Validated RoleRequest roleRequest) {
        return ApiResponse.<RoleResponse>builder()
                .data(roleService.createRole(roleRequest))
                .build();
    }

    @PutMapping(ApiPath.ROLE_ID_UPDATE)
    ApiResponse<RoleResponse> updateRole(@RequestBody @Validated RoleRequest roleRequest, @PathVariable String id) {
        return ApiResponse.<RoleResponse>builder()
                .data(roleService.updateRole(id, roleRequest))
                .build();
    }

    @DeleteMapping(ApiPath.ROLE_ID_DELETE)
    void deleteRole(@PathVariable String id) {
        roleService.deleteRole(id);
    }

}
