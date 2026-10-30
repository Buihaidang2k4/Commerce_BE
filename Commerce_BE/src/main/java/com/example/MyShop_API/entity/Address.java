package com.example.MyShop_API.entity;

import com.example.MyShop_API.Enum.AddressType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    Long addressId;

    String fullName;
    String phone;
    String street;

    Integer wardCode;
    String ward;
    Integer districtID;
    String district;
    Integer provinceID;
    String province;

    String postalCode;

    @Column(name = "additional_info", columnDefinition = "TEXT")
    String additionalInfo; // note

    @Column(name = "is_default")
    Boolean isDefault = false;

    @Enumerated(EnumType.STRING)
    AddressType type = AddressType.HOME;
    String label;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "profile_id", nullable = false)
    UserProfile profile;

    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    LocalDateTime updatedAt = LocalDateTime.now();

}