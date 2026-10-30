package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "password_reset_otp")
public class PasswordResetOtp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    User user;

    @Column(nullable = false, length = 6)
    String otp;

    @Column(nullable = false)
    LocalDateTime createdAt;

    @Column(nullable = false)
    LocalDateTime expiredAt;

    @Column(nullable = false)
    boolean used = false;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredAt);
    }

    @PrePersist
    public void setExpired() {
        if (this.expiredAt == null) {
            this.expiredAt = this.createdAt.plusMinutes(5);
        }
    }

    public boolean isValid() {
        return !used && !isExpired();
    }
}
