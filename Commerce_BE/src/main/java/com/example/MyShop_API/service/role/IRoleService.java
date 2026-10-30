package com.example.MyShop_API.service.role;

import com.example.MyShop_API.dto.request.RoleRequest;
import com.example.MyShop_API.dto.response.RoleResponse;

import java.util.List;

public interface IRoleService {
    List<RoleResponse> getAllRoles();

    RoleResponse getRoleById(String id);

    RoleResponse createRole(RoleRequest roleRequest);

    RoleResponse updateRole(String id, RoleRequest roleRequest);

    void deleteRole(String id);
}
