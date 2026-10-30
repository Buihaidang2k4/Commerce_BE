package com.example.MyShop_API.service.password_reset_otp;

import com.example.MyShop_API.dto.request.ResetPasswordRequest;
import com.example.MyShop_API.entity.PasswordResetOtp;
import com.example.MyShop_API.entity.User;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.constants.CommonMessageUtils;
import com.example.MyShop_API.repo.PasswordResetOtpRepository;
import com.example.MyShop_API.repo.UserRepository;
import com.example.MyShop_API.utils.OTPGeneratorUntil;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PasswordResetOtpService implements IPasswordResetOtpService {
    UserRepository userRepository;
    PasswordResetOtpRepository otpRepository;
    EmailService emailService;
    PasswordEncoder passwordEncoder;

    // ================ SEND OTP ====================
    @Transactional
    @Override
    public void sendOtp(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(CommonMessageUtils.EMAIL_NOT_FOUND));

        // delete oldOTP
        otpRepository.deleteAllByUser(user);

        // gen OTP
        String otp = OTPGeneratorUntil.generateOTP(6);

        PasswordResetOtp resetOtp = PasswordResetOtp.builder()
                .user(user)
                .otp(otp)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(5))
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
        PasswordResetOtp otpEntity = otpRepository.findByUserEmailAndOtp(request.getEmail(), request.getOtp())
                .orElseThrow(() -> new AppException(CommonMessageUtils.OTP_INVALID));

        if (otpEntity.isUsed() || otpEntity.isExpired()) {
            throw new AppException(CommonMessageUtils.OTP_EXPIRED);
        }

        User user = otpEntity.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        otpEntity.setUsed(true);
        otpRepository.save(otpEntity);

        log.info("Password reset successful for user {} ,{}", user.getId(), user.getPassword());
    }


}
