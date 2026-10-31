package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "shops", indexes = {@Index(name = "idx_shops_owner_user_id", columnList = "owner_user_id"), @Index(name = "idx_shops_slug", columnList = "slug"), @Index(name = "idx_shops_approved_by_user_id", columnList = "approved_by_user_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Shop extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long ownerUserId;
    private String name;
    private String slug;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String status;
    private Long approvedByUserId;
    private Instant approvedAt;


}
