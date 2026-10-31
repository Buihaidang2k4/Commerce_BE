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
public abstract class AuditableEntity extends CreatedEntity {
    private Instant updatedAt;
    private String updatedBy;

    @PrePersist
    protected void initializeUpdateAudit() {
        if (updatedAt == null) updatedAt = Instant.now();
        if (updatedBy == null) updatedBy = AuditActor.current();
    }
    @PreUpdate
    protected void updateAudit() {
        updatedAt = Instant.now();
        updatedBy = AuditActor.current();
    }
}
