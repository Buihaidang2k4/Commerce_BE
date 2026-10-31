package com.example.MyShop_API.controller;

import com.example.MyShop_API.constants.ApiPathConstants;
import com.example.MyShop_API.dto.request.PermissionRequest;
import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.entity.Permission;
import com.example.MyShop_API.service.permission.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPathConstants.BASE_PERMISSION)
public class PermissionController {
    private final PermissionService permissionService;

    @GetMapping
    public ApiResponse<List<Permission>> getAll() {
        return ApiResponse.<List<Permission>>builder().code(200).data(permissionService.getAll()).build();
    }
    @PostMapping
    public ApiResponse<Permission> create(@Valid @RequestBody PermissionRequest request) {
        return ApiResponse.<Permission>builder().code(200).data(permissionService.create(request)).build();
    }
    @PutMapping(ApiPathConstants.PERMISSION_ID)
    public ApiResponse<Permission> update(@PathVariable Long id, @Valid @RequestBody PermissionRequest request) {
        return ApiResponse.<Permission>builder().code(200).data(permissionService.update(id, request)).build();
    }
    @DeleteMapping(ApiPathConstants.PERMISSION_ID)
    public void delete(@PathVariable Long id) { permissionService.delete(id); }
}
