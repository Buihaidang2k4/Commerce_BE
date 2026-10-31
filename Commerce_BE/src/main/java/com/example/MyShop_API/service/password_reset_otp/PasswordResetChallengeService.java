package com.example.MyShop_API.service.password_reset_otp;

import com.example.MyShop_API.constants.AuthConstants;
import com.example.MyShop_API.constants.CommonConstants;

import com.example.MyShop_API.constants.CommonMessageConstants;
import com.example.MyShop_API.dto.request.ResetPasswordRequest;
import com.example.MyShop_API.entity.PasswordResetChallenge;
import com.example.MyShop_API.entity.UserAccount;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.repo.PasswordResetChallengeRepository;
import com.example.MyShop_API.repo.RefreshSessionRepository;
import com.example.MyShop_API.repo.UserAccountRepository;
import com.example.MyShop_API.utils.OTPGeneratorUntil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import java.time.Instant;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetChallengeService implements IPasswordResetChallengeService {
    private final UserAccountRepository userRepository;
    private final PasswordResetChallengeRepository otpRepository;
    private final RefreshSessionRepository sessionRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final PlatformTransactionManager transactionManager;

    @Scheduled(cron = AuthConstants.OTP_CLEANUP_CRON, zone = CommonConstants.TIME_ZONE_UTC)
    @Transactional
    public void cleanupExpiredChallenges() {
        otpRepository.deleteExpiredBefore(Instant.now().minusSeconds(AuthConstants.OTP_RETENTION_SECONDS));
    }

    @Override
    public void sendOtp(String email) {
        TransactionTemplate creation = new TransactionTemplate(transactionManager);
        creation.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        OtpMail mail = creation.execute(status -> createChallenge(email));
        if (mail == null) return;
        // The transaction and connection are released before contacting SMTP.
        try {
            emailService.sendOtpMail(mail.recipient(), mail.otp(), mail.recipient());
        } catch (MessagingException | MailException ex) {
            removeUndeliveredChallenge(mail.challengeId());
            throw new AppException(CommonMessageConstants.AUTH_UNAVAILABLE);
        }
    }

    private record OtpMail(Long challengeId, String recipient, String otp) {}

    private OtpMail createChallenge(String email) {
        UserAccount user = userRepository.findByEmailForUpdate(email.trim().toLowerCase(Locale.ROOT)).orElse(null);
        // Same external response for absent, locked and active accounts.
        if (user == null || !user.isEnabled()) return null;
        PasswordResetChallenge previous = otpRepository.findFirstByUserIdOrderByIdDesc(user.getId()).orElse(null);
        if (previous != null && previous.getCreatedAt() != null && previous.getCreatedAt().isAfter(Instant.now().minusSeconds(AuthConstants.OTP_RESEND_SECONDS))) return null;
        otpRepository.deleteAllByUserId(user.getId());
        otpRepository.flush();
        String otp = OTPGeneratorUntil.generateOTP(AuthConstants.OTP_LENGTH);
        PasswordResetChallenge challenge = otpRepository.saveAndFlush(PasswordResetChallenge.builder().userId(user.getId()).otpHash(passwordEncoder.encode(otp))
                .expiresAt(Instant.now().plusSeconds(AuthConstants.OTP_TTL_SECONDS)).used(false).failedAttempts(0).build());
        return new OtpMail(challenge.getId(), user.getEmail(), otp);
    }

    private void removeUndeliveredChallenge(Long challengeId) {
        TransactionTemplate cleanup = new TransactionTemplate(transactionManager);
        cleanup.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        try {
            // Match this exact challenge so a newer request is never removed.
            cleanup.executeWithoutResult(status -> otpRepository.deleteUndeliveredById(challengeId));
        } catch (RuntimeException ex) {
            log.warn("Could not remove undelivered password reset challenge {}", challengeId);
        }
    }

    @Override
    @Transactional(noRollbackFor = AppException.class)
    public void resetPassword(ResetPasswordRequest request) {
        UserAccount user = userRepository.findByEmailForUpdate(request.getEmail().trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new AppException(CommonMessageConstants.OTP_INVALID));
        if (!user.isEnabled()) throw new AppException(CommonMessageConstants.OTP_INVALID);
        PasswordResetChallenge challenge = otpRepository.findFirstByUserIdOrderByIdDesc(user.getId())
                .orElseThrow(() -> new AppException(CommonMessageConstants.OTP_INVALID));
        int attempts = challenge.getFailedAttempts() == null ? 0 : challenge.getFailedAttempts();
        if (!challenge.isValid() || attempts >= AuthConstants.OTP_MAX_ATTEMPTS || challenge.getOtpHash() == null)
            throw new AppException(CommonMessageConstants.OTP_EXPIRED);
        if (!passwordEncoder.matches(request.getOtp(), challenge.getOtpHash())) {
            challenge.setFailedAttempts(attempts + 1);
            if (attempts + 1 >= AuthConstants.OTP_MAX_ATTEMPTS) challenge.setUsed(true);
            otpRepository.save(challenge);
            throw new AppException(CommonMessageConstants.OTP_INVALID);
        }
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        challenge.setUsed(true);
        otpRepository.save(challenge);
        sessionRepository.revokeAllByUserId(user.getId(), Instant.now());
    }
}
