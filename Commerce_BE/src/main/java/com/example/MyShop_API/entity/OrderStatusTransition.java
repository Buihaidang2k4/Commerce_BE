package com.example.MyShop_API.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.*;
import java.sql.Blob;

@Entity
@Table(name = "order_status_transitions", indexes = {@Index(name = "idx_order_status_transitions_order_id", columnList = "order_id"), @Index(name = "idx_order_status_transitions_actor_user_id", columnList = "actor_user_id")})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusTransition extends CreatedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private String fromStatus;
    private String toStatus;
    private Long actorUserId;
    private String reason;
    private Instant occurredAt;


}
