package com.example.MyShop_API.utils;

import java.security.SecureRandom;
import java.util.Random;

public class RadomCodeTemplate {
    private static final String DIGITSBARCODE = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITSNUMBER = "0123456789";

    public static String radomBarcode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(DIGITSBARCODE.charAt(RANDOM.nextInt(DIGITSBARCODE.length())));
        }
        return sb.toString();
    }

    // Hàm tạo 2 chữ cái ngẫu nhiên
    private static String randomLetters(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(LETTERS.charAt(RANDOM.nextInt(LETTERS.length())));
        }
        return sb.toString();
    }

    // Hàm tạo số ngẫu nhiên có đúng số chữ số
    private static String randomDigits(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(DIGITSNUMBER.charAt(RANDOM.nextInt(DIGITSNUMBER.length())));
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
