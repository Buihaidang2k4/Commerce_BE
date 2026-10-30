package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "shops", indexes = {@Index(name = "idx_shops_owner_user_id", columnList = "owner_user_id"), @Index(name = "idx_shops_slug", columnList = "slug"), @Index(name = "idx_shops_approved_by_user_id", columnList = "approved_by_user_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long ownerUserId;
    private String name;
    private String slug;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Builder.Default
    private String status = "PENDING";
    private Long approvedByUserId;
    private Instant approvedAt;
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
