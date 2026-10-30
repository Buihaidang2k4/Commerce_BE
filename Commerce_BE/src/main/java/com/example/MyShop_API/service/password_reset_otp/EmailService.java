package com.example.MyShop_API.service.password_reset_otp;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {
    JavaMailSender mailSender;

    public void sendOtpMail(String to, String otp, String username) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("🔒 MyShop - Đặt lại mật khẩu");

        String htmlContent = """
                <html>
                <body style="font-family: Arial, sans-serif; line-height:1.6;">
                    <h2 style="color:#2E86C1;">Chào %s,</h2>
                    <p>Bạn đã yêu cầu <strong>đặt lại mật khẩu</strong> cho tài khoản MyShop.</p>
                    <p style="font-size: 18px; font-weight: bold; background-color: #f2f2f2; padding: 10px; display: inline-block; border-radius: 5px;">
                        OTP của bạn: %s
                    </p>
                    <p>Mã OTP có hiệu lực trong <strong>5 phút</strong>.</p>
                    <p>Nếu bạn không yêu cầu, vui lòng <em>bỏ qua email này</em>.</p>
                    <br>
                    <p>Trân trọng,<br>MyShop Team</p>
                </body>
                </html>
                """.formatted(username, otp);

        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

}
