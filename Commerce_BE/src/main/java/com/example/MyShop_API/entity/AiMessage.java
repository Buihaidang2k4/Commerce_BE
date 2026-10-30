package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ai_messages", indexes = {@Index(name = "idx_ai_messages_conversation_id", columnList = "conversation_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long conversationId;
    private String role;
    @Column(columnDefinition = "TEXT")
    private String content;
    @Column(columnDefinition = "TEXT")
    private String retrievalJson;
    private String model;
    private Integer inputTokens;
    private Integer outputTokens;
    private String status;
    @Builder.Default
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
