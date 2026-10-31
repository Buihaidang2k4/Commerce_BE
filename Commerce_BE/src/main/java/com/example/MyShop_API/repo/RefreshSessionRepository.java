package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.RefreshSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.Optional;

public interface RefreshSessionRepository extends JpaRepository<RefreshSession, Long> {
    @Modifying
    @Query("DELETE FROM RefreshSession s WHERE s.expiresAt < :cutoff")
    int deleteExpiredBefore(@Param("cutoff") Instant cutoff);
    Optional<RefreshSession> findByTokenHash(String tokenHash);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM RefreshSession s WHERE s.id = :id")
    Optional<RefreshSession> findByIdForUpdate(@Param("id") Long id);
    interface SessionOwner {
        Long getId();
        Long getUserId();
    }
    @Query("SELECT s.id AS id, s.userId AS userId FROM RefreshSession s WHERE s.tokenHash = :hash")
    Optional<SessionOwner> findOwnerByTokenHash(@Param("hash") String hash);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE RefreshSession s SET s.revokedAt = :now, s.updatedAt = :now, s.updatedBy = 'system' WHERE s.userId = :userId AND s.revokedAt IS NULL")
    int revokeAllByUserId(@Param("userId") Long userId, @Param("now") Instant now);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE RefreshSession s SET s.revokedAt = :now, s.updatedAt = :now, s.updatedBy = 'system' WHERE s.userId = :userId AND s.tokenFamilyId = :family AND s.revokedAt IS NULL")
    int revokeFamily(@Param("userId") Long userId, @Param("family") String family, @Param("now") Instant now);
}
