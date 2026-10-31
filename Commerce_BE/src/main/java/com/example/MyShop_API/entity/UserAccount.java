package com.example.MyShop_API.entity;

import com.example.MyShop_API.constants.AccountStatusConstants;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "users", indexes = {@Index(name = "idx_users_email", columnList = "email")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonIgnore
    private String passwordHash;
    private String email;
    private String accountStatus;
    private String lockReason;
    private Instant lockedAt;

    public boolean isEnabled() { return AccountStatusConstants.ACTIVE.equals(accountStatus); }
    public void setEnabled(boolean enabled) { accountStatus = enabled ? AccountStatusConstants.ACTIVE : AccountStatusConstants.LOCKED; }
}
