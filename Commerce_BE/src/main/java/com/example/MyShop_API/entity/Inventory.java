package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inventory", indexes = @Index(columnList = "product_id"))
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long inventoryId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", unique = true, nullable = false)
    Product product;
    int available = 0;
    int reserved = 0; // số lượng đặt trước chưa xuất kho (giỏ hàng, đơn hàng)
    LocalDateTime updatedAt;

    // kiểm tra xem có thể đặt thêm không
    public boolean canReserve(int quantity) {
        return available >= quantity;
    }
}
