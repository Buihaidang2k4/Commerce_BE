package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.PasswordResetChallenge;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;
import jakarta.persistence.LockModeType;
import java.util.*;

public interface PasswordResetChallengeRepository extends JpaRepository<PasswordResetChallenge, Long> {
    void deleteByUserId(Long userId);
    void deleteAllByUserId(Long userId);
    @Query("SELECT p FROM PasswordResetChallenge p WHERE p.userId IN (SELECT u.id FROM UserAccount u WHERE u.email = :email) AND p.otp = :otp")
    Optional<PasswordResetChallenge> findByUserEmailAndOtp(@Param("email") String email, @Param("otp") String otp);
}
