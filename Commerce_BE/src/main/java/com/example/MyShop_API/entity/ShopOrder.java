package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "shop_orders", indexes = {@Index(name = "idx_shop_orders_order_id", columnList = "order_id"), @Index(name = "idx_shop_orders_shop_id", columnList = "shop_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ShopOrder extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private Long shopId;
    private String status;
    @Column(precision = 18, scale = 2)
    private BigDecimal subtotalAmount;
    @Column(precision = 18, scale = 2)
    private BigDecimal shippingAmount;
    @Column(precision = 18, scale = 2)
    private BigDecimal discountAmount;
    @Column(precision = 18, scale = 2)
    private BigDecimal totalAmount;
    @Version
    private Long version;


}
