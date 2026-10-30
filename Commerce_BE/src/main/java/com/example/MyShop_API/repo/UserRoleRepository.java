package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    java.util.List<UserRole> findByUserId(Long id);
    void deleteByUserId(Long id);
}
