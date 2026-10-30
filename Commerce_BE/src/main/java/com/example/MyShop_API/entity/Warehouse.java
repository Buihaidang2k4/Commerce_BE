package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "warehouses", indexes = {@Index(name = "idx_warehouses_shop_id", columnList = "shop_id"), @Index(name = "idx_warehouses_address_id", columnList = "address_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long shopId;
    private String name;
    private Long addressId;
    @Builder.Default
    private String status = "ACTIVE";
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
