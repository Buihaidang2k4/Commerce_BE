package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
}
