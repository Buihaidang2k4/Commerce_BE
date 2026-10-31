package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "shopping_carts", indexes = {@Index(name = "idx_shopping_carts_user_profile_id", columnList = "user_profile_id"), @Index(name = "idx_shopping_carts_user_id", columnList = "user_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCart extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userProfileId;
    private Long userId;
    @Column(precision = 18, scale = 2)
    private BigDecimal subtotalAmount;


}
