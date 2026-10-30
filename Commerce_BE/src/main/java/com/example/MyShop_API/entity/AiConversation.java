package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ai_conversations", indexes = {@Index(name = "idx_ai_conversations_user_id", columnList = "user_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiConversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String title;
    @Builder.Default
    private String status = "ACTIVE";
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
