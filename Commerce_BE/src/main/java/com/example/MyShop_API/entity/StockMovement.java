package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "stock_movements", indexes = {@Index(name = "idx_stock_movements_variant_id", columnList = "variant_id"), @Index(name = "idx_stock_movements_product_id", columnList = "product_id"), @Index(name = "idx_stock_movements_reference_id", columnList = "reference_id"), @Index(name = "idx_stock_movements_actor_user_id", columnList = "actor_user_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovement extends CreatedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long variantId;
    private Long productId;
    private Integer quantityDelta;
    private String reason;
    private String referenceType;
    private Long referenceId;
    private Long actorUserId;


}
