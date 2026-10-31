package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.Instant;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public abstract class CreatedEntity {
    @Column(updatable = false)
    private Instant createdAt;
    @Column(updatable = false)
    private String createdBy;

    @PrePersist
    protected void initializeCreationAudit() {
        if (createdAt == null) createdAt = Instant.now();
        if (createdBy == null) createdBy = AuditActor.current();
    }
}
