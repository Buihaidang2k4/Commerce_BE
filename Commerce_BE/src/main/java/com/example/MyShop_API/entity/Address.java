package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "addresses", indexes = {@Index(name = "idx_addresses_profile_id", columnList = "profile_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;
    private Long profileId;
    private String fullName;
    private String phone;
    private String street;
    private Integer wardCode;
    private String ward;
    private Integer districtID;
    private String district;
    private Integer provinceID;
    private String province;
    private String postalCode;
    @Column(columnDefinition = "TEXT")
    private String additionalInfo;
    @Builder.Default
    private Boolean isDefault = false;
    @Builder.Default
    private String type = "HOME";
    private String label;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

}
