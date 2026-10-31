package com.example.MyShop_API.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.Locale;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonConstants {
    public static final String DIGITS = "0123456789";
    public static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String BARCODE_CHARACTERS = DIGITS + UPPERCASE_LETTERS;
    public static final String TIME_ZONE_UTC = "UTC";
    public static final String LOCALE_HEADER = "locale";
    public static final String MESSAGE_BUNDLE = "messages";
    public static final String ENGLISH_LANGUAGE = "en";
    public static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("vi");
    public static final Set<String> SAFE_HTTP_METHODS = Set.of("GET", "HEAD", "OPTIONS");
    public static final Set<String> AUTH_RATE_LIMIT_METHODS = Set.of("POST", "PUT");
}
