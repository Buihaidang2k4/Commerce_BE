package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "order_delivery_addresses", indexes = {@Index(name = "idx_order_delivery_addresses_order_id", columnList = "order_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDeliveryAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private String recipientName;
    private String recipientPhone;
    private String street;
    private String ward;
    private String district;
    private String province;
    private String postalCode;
    @Column(columnDefinition = "TEXT")
    private String deliveryNote;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

}
