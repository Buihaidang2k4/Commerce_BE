package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.Instant;

@Entity
@Table(name = "password_reset_challenges", indexes = {@Index(name = "idx_password_reset_challenges_expires_at", columnList = "expires_at"), @Index(name = "idx_password_reset_challenges_user_id", columnList = "user_id")})
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
    private String otpHash;
    private Instant expiresAt;
    private boolean used;
    private Integer failedAttempts;

    public boolean isExpired() { return expiresAt == null || !Instant.now().isBefore(expiresAt); }
    public boolean isValid() { return !used && !isExpired(); }
}
