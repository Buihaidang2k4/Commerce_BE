package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "role_permissions", indexes = {@Index(name = "idx_role_permissions_permission_id", columnList = "permission_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String roleName;
    private Long permissionId;
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
