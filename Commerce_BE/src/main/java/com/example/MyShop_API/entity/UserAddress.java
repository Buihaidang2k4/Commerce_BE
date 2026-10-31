package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "user_addresses", indexes = {@Index(name = "idx_user_addresses_user_profile_id", columnList = "user_profile_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserAddress extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userProfileId;
    private String recipientName;
    private String recipientPhone;
    private String street;
    private Integer wardCode;
    private String ward;
    private Integer districtCode;
    private String district;
    private Integer provinceCode;
    private String province;
    private String postalCode;
    @Column(columnDefinition = "TEXT")
    private String deliveryInstructions;
    private Boolean defaultShippingAddress;
    private String addressType;
    private String label;


}
