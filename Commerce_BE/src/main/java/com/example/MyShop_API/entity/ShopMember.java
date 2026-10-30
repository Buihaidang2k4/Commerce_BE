package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "shop_members", indexes = {@Index(name = "idx_shop_members_shop_id", columnList = "shop_id"), @Index(name = "idx_shop_members_user_id", columnList = "user_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long shopId;
    private Long userId;
    private String role;
    @Builder.Default
    private String status = "ACTIVE";
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
