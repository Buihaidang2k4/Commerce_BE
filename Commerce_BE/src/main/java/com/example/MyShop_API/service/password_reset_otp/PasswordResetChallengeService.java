package com.example.MyShop_API.service.password_reset_otp;

import com.example.MyShop_API.dto.request.ResetPasswordRequest;
import com.example.MyShop_API.entity.PasswordResetChallenge;
import com.example.MyShop_API.entity.UserAccount;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.constants.CommonMessageUtils;
import com.example.MyShop_API.repo.PasswordResetChallengeRepository;
import com.example.MyShop_API.repo.UserAccountRepository;
import com.example.MyShop_API.utils.OTPGeneratorUntil;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PasswordResetChallengeService implements IPasswordResetChallengeService {
    private final com.example.MyShop_API.service.model.ModelLookup models;
    UserAccountRepository userRepository;
    PasswordResetChallengeRepository otpRepository;
    EmailService emailService;
    PasswordEncoder passwordEncoder;

    // ================ SEND OTP ====================
    @Transactional
    @Override
    public void sendOtp(String email) {
        UserAccount user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(CommonMessageUtils.EMAIL_NOT_FOUND));

        // delete oldOTP
        otpRepository.deleteAllByUserId(user.getId());

        // gen OTP
        String otp = OTPGeneratorUntil.generateOTP(6);

        PasswordResetChallenge resetOtp = PasswordResetChallenge.builder()
                .userId(user.getId())
                .otp(otp)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plus(5, java.time.temporal.ChronoUnit.MINUTES))
                .build();

        otpRepository.save(resetOtp);

        // send email
        try {
            emailService.sendOtpMail(user.getEmail(), otp, user.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send OTP email to {}", email, e);
            throw new RuntimeException(e);
        }
        log.info("OTP sent to user {}", user.getId());
    }

    // ============== RESET PASSWORD =================
    @Override
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetChallenge otpEntity = otpRepository.findByUserEmailAndOtp(request.getEmail(), request.getOtp())
                .orElseThrow(() -> new AppException(CommonMessageUtils.OTP_INVALID));

        if (otpEntity.isUsed() || otpEntity.isExpired()) {
            throw new AppException(CommonMessageUtils.OTP_EXPIRED);
        }

        UserAccount user = models.user(otpEntity.getUserId());
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        otpEntity.setUsed(true);
        otpRepository.save(otpEntity);

        log.info("Password reset successful for user {} ,{}", user.getId(), user.getPasswordHash());
    }


}
