package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "user", indexes = {@Index(name = "idx_user_email", columnList = "email")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @com.fasterxml.jackson.annotation.JsonIgnore
    private String password;
    private String email;
    @Builder.Default
    private boolean enabled = true;
    @Builder.Default
    private String status = "ACTIVE";
    private String lockedReason;
    private LocalDateTime lockedAt;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

}
