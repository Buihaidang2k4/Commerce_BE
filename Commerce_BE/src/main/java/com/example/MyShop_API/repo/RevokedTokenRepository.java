package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface RevokedTokenRepository extends JpaRepository<RevokedToken, Long> {
    boolean existsByToken(String token);

    @Modifying
    @Query("DELETE FROM RevokedToken t WHERE t.expiresAt < :now")
    int deleteExpiredTokens(@Param("now") Instant now);

    @Query("SELECT r FROM RevokedToken r WHERE r.expiresAt > :now")
    List<RevokedToken> findAllValid(@Param("now") Instant now);

    Optional<RevokedToken> findByToken(String token);
}
