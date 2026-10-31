package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    java.util.Optional<Role> findByCode(String code);
    java.util.List<Role> findByCodeIn(java.util.Collection<String> codes);
    void deleteByCode(String code);
}
