package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "user_profile", indexes = {@Index(name = "idx_user_profile_user_id", columnList = "user_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;
    private Long userId;
    private String username;
    private Boolean gender;
    private LocalDate birthDate;
    private String mobileNumber;

}
