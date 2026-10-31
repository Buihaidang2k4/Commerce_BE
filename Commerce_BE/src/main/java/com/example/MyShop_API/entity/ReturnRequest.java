package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "return_requests", indexes = {@Index(name = "idx_return_requests_order_id", columnList = "order_id"), @Index(name = "idx_return_requests_user_id", columnList = "user_id"), @Index(name = "idx_return_requests_shop_id", columnList = "shop_id"), @Index(name = "idx_return_requests_reviewed_by_user_id", columnList = "reviewed_by_user_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnRequest extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private Long userId;
    private Long shopId;
    private String status;
    private String reason;
    private Long reviewedByUserId;
    private Instant reviewedAt;
    @Version
    private Long version;


}
