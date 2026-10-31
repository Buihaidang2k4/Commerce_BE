package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "stock_balances", indexes = {@Index(name = "idx_stock_balances_product_id", columnList = "product_id"), @Index(name = "idx_stock_balances_variant_id", columnList = "variant_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class StockBalance extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private Long variantId;
    private int onHandQuantity;
    private int reservedQuantity;
    @Version
    private Long version;

    public boolean canReserve(int quantity) { return quantity > 0 && getAvailableQuantity() >= quantity; }
    public int getAvailableQuantity() { return onHandQuantity - reservedQuantity; }
}
