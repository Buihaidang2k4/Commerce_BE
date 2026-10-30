package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "refresh_tokens", indexes = {@Index(name = "idx_refresh_tokens_user_id", columnList = "user_id"), @Index(name = "idx_refresh_tokens_family_id", columnList = "family_id"), @Index(name = "idx_refresh_tokens_replaced_by_token_id", columnList = "replaced_by_token_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String tokenHash;
    private String familyId;
    private Instant expiresAt;
    private Instant revokedAt;
    private Long replacedByTokenId;
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
