package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.PasswordResetOtp;
import com.example.MyShop_API.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {
    void deleteByUser(User user);

    Optional<PasswordResetOtp> findByUserEmailAndOtp(String email, String otp);

    void deleteAllByUser(User user);
}
