package com.example.MyShop_API.utils;

import java.security.SecureRandom;

public class OTPGeneratorUntil {
    private static final SecureRandom radom = new SecureRandom();

    // gen otp forgot password email
    public static String generateOTP(int length) {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int digit = radom.nextInt(10);
            otp.append(digit);
        }
        return otp.toString();
    }
}
