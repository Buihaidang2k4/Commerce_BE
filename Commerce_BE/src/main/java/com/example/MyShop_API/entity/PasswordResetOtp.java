package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "password_reset_otp", indexes = {@Index(name = "idx_password_reset_otp_user_id", columnList = "user_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetOtp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String otp;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime expiredAt;
    @Builder.Default
    private boolean used = false;
    public boolean isExpired() { return expiredAt == null || LocalDateTime.now().isAfter(expiredAt); }
    public boolean isValid() { return !used && !isExpired(); }
    @PrePersist public void initializeExpiry() { if (createdAt == null) createdAt = LocalDateTime.now(); if (expiredAt == null) expiredAt = createdAt.plusMinutes(5); }
}
