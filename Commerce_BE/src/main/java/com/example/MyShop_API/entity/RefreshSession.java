package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "refresh_sessions", indexes = {@Index(name = "idx_refresh_sessions_user_id", columnList = "user_id"), @Index(name = "idx_refresh_sessions_token_family_id", columnList = "token_family_id"), @Index(name = "idx_refresh_sessions_replaced_by_session_id", columnList = "replaced_by_session_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshSession extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String tokenHash;
    private String tokenFamilyId;
    private Instant expiresAt;
    private Instant revokedAt;
    private Long replacedBySessionId;


}
