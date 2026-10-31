package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.TokenRevocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRevocationRepository extends JpaRepository<TokenRevocation, Long> {
    boolean existsByToken(String token);

    @Modifying
    @Query("DELETE FROM TokenRevocation t WHERE t.expiresAt < :now")
    int deleteExpiredTokens(@Param("now") Instant now);

    @Query("SELECT r FROM TokenRevocation r WHERE r.expiresAt > :now")
    List<TokenRevocation> findAllValid(@Param("now") Instant now);

    Optional<TokenRevocation> findByToken(String token);
}
