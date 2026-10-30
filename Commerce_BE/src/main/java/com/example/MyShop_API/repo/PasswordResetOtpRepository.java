package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.PasswordResetOtp;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;
import jakarta.persistence.LockModeType;
import java.util.*;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {
    void deleteByUserId(Long userId);
    void deleteAllByUserId(Long userId);
    @Query("SELECT p FROM PasswordResetOtp p WHERE p.userId IN (SELECT u.id FROM User u WHERE u.email = :email) AND p.otp = :otp")
    Optional<PasswordResetOtp> findByUserEmailAndOtp(@Param("email") String email, @Param("otp") String otp);
}
