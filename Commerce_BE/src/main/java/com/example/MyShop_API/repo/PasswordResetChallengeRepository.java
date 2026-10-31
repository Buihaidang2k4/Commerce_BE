package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.PasswordResetChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.time.Instant;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PasswordResetChallengeRepository extends JpaRepository<PasswordResetChallenge, Long> {
    @Modifying
    @Query("DELETE FROM PasswordResetChallenge p WHERE p.id = :id AND p.used = false")
    int deleteUndeliveredById(@Param("id") Long id);
    @Modifying
    @Query("DELETE FROM PasswordResetChallenge p WHERE p.expiresAt < :cutoff")
    int deleteExpiredBefore(@Param("cutoff") Instant cutoff);
    void deleteByUserId(Long userId);
    void deleteAllByUserId(Long userId);
    Optional<PasswordResetChallenge> findFirstByUserIdOrderByIdDesc(Long userId);
}
