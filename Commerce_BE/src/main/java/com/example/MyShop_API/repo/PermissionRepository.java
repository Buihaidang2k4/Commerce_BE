package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.Set;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByCode(String code);
    @Query("SELECT p FROM Permission p WHERE p.id IN (SELECT rp.permissionId FROM RolePermission rp WHERE rp.roleId IN (SELECT ur.roleId FROM UserRole ur WHERE ur.userId = :userId))")
    Set<Permission> findAllByUserId(@Param("userId") Long userId);
}
