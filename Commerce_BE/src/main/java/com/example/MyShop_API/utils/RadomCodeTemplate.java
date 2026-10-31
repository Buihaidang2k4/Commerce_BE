package com.example.MyShop_API.utils;

import com.example.MyShop_API.constants.CommonConstants;

import java.security.SecureRandom;

public class RadomCodeTemplate {
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String radomBarcode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(CommonConstants.BARCODE_CHARACTERS.charAt(RANDOM.nextInt(CommonConstants.BARCODE_CHARACTERS.length())));
        }
        return sb.toString();
    }

    // Hàm tạo 2 chữ cái ngẫu nhiên
    private static String randomLetters(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(CommonConstants.UPPERCASE_LETTERS.charAt(RANDOM.nextInt(CommonConstants.UPPERCASE_LETTERS.length())));
        }
        return sb.toString();
    }

    // Hàm tạo số ngẫu nhiên có đúng số chữ số
    private static String randomDigits(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(CommonConstants.DIGITS.charAt(RANDOM.nextInt(CommonConstants.DIGITS.length())));
        }
        return sb.toString();
    }

    // Hàm tạo chuỗi theo định dạng HC-51-03-GV13
    public static String generateCode() {
        String part1 = randomLetters(2);       // 2 chữ cái
        String part2 = randomDigits(2);        // 2 số
        String part3 = randomDigits(2);        // 2 số
        String part4 = randomLetters(2) + randomDigits(2); // 2 chữ + 2 số

        return part1 + "-" + part2 + "-" + part3 + "-" + part4;
    }
}
