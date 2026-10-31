package com.example.MyShop_API.repo;

import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Optional;

import com.example.MyShop_API.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Role r WHERE r.code = :code")
    Optional<Role> findByCodeForUpdate(@Param("code") String code);
    java.util.Optional<Role> findByCode(String code);
    java.util.List<Role> findByCodeIn(java.util.Collection<String> codes);
    void deleteByCode(String code);
    @Query("SELECT r FROM Role r WHERE r.id IN (SELECT ur.roleId FROM UserRole ur WHERE ur.userId = :userId)")
    Set<Role> findAllByUserId(@Param("userId") Long userId);
}
