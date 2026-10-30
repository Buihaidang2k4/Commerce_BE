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
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "order_delivery_addresses")
public class OrderDeliveryAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    Order order;

    @Column(nullable = false)
    String recipientName;

    @Column(nullable = false, length = 20)
    String recipientPhone;

    @Column(nullable = false)
    String street;           // Số nhà, tên đường

    @Column(nullable = false)
    String ward;             // Phường/Xã

    @Column(nullable = false)
    String district;         // Quận/Huyện

    @Column(nullable = false)
    String province;         // Tỉnh/Thành phố

    String postalCode;       // Có thể null ở VN

    @Column(columnDefinition = "TEXT")
    String deliveryNote;     // Ghi chú: "Giao sau 17h", "Để trước cửa"...

    LocalDateTime createdAt = LocalDateTime.now();
}
