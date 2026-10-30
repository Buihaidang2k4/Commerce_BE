package com.example.MyShop_API.service.password_reset_otp;

import com.example.MyShop_API.dto.request.ResetPasswordRequest;

public interface IPasswordResetOtpService {
    void sendOtp(String email);

    void resetPassword(ResetPasswordRequest request);
}
