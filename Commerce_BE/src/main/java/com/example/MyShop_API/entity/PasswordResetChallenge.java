package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "password_reset_challenges", indexes = {@Index(name = "idx_password_reset_challenges_user_id", columnList = "user_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetChallenge extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String otp;
    private Instant expiresAt;
    private boolean used;

    public boolean isExpired() { return expiresAt == null || !Instant.now().isBefore(expiresAt); }
    public boolean isValid() { return !used && !isExpired(); }
    @PrePersist public void initializeExpiry() { if (expiresAt == null) expiresAt = Instant.now().plusSeconds(300); }
}
