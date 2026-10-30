package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "permissions", indexes = {@Index(name = "idx_permissions_code", columnList = "code")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String resource;
    private String action;
    private String scope;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
