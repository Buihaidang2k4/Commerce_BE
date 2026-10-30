package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "carts", indexes = {@Index(name = "idx_carts_profile_id", columnList = "profile_id"), @Index(name = "idx_carts_user_id", columnList = "user_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;
    private Long profileId;
    private Long userId;
    @Builder.Default
    @Column(precision = 18, scale = 2)
    private BigDecimal totalPrice = BigDecimal.ZERO;
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

}
