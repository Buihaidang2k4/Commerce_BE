package com.example.MyShop_API.utils;

import java.text.Normalizer;

public class SlugUtils {
    public static String toSlug(String input) {
        if (input == null || input.isEmpty()) return "";

        String slug = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCOMBINING_DIACRITICAL_MARKS}+", "") // bỏ dấu tiếng việt
                .replaceAll("đ", "d")
                .replaceAll("Đ", "d")
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "") // bỏ ký tự không hợp lệ
                .replaceAll("\\s+", "-") // space → dấu '-'
                .replaceAll("-+", "-") // gộp dấu '-'
                .replaceAll("^-|-$", ""); // bỏ '-' ở đầu/cuối

        return slug;
    }

}
