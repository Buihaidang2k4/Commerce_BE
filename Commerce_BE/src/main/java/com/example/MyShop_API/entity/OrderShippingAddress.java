package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "order_shipping_addresses", indexes = {@Index(name = "idx_order_shipping_addresses_order_id", columnList = "order_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OrderShippingAddress extends AuditableEntity {
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
    private String deliveryInstructions;


}
