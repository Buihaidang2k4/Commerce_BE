package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    List<RolePermission> findByRoleId(Long roleId);
    void deleteByRoleId(Long roleId);
    void deleteByPermissionId(Long permissionId);
}
