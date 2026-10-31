package com.example.MyShop_API.exception;

import com.example.MyShop_API.constants.CommonConstants;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.Locale;
import java.nio.charset.StandardCharsets;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageHandlerUtils {
    private static final ResourceBundleMessageSource SOURCE = createSource();

    private static ResourceBundleMessageSource createSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename(CommonConstants.MESSAGE_BUNDLE);
        source.setDefaultEncoding(StandardCharsets.UTF_8.name());
        source.setFallbackToSystemLocale(false);
        return source;
    }

    public static MessageSource getMessageSource() {
        return SOURCE;
    }

    public static Locale resolveLocale(HttpServletRequest request) {
        String header = request == null ? null : request.getHeader(CommonConstants.LOCALE_HEADER);
        if (header == null || header.isBlank()) return CommonConstants.DEFAULT_LOCALE;
        Locale locale = Locale.forLanguageTag(header.trim());
        return CommonConstants.ENGLISH_LANGUAGE.equals(locale.getLanguage()) ? Locale.ENGLISH : CommonConstants.DEFAULT_LOCALE;
    }

    public static Locale resolveLocale() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
            return resolveLocale(attrs.getRequest());
        }
        return CommonConstants.DEFAULT_LOCALE;
    }

    public static String getMessage(String code, Locale locale, Object... args) {
        Locale supported = locale != null && CommonConstants.ENGLISH_LANGUAGE.equals(locale.getLanguage()) ? Locale.ENGLISH : CommonConstants.DEFAULT_LOCALE;
        return SOURCE.getMessage(code, args, code, supported);
    }

    public static String getMessage(String code) {
        return getMessage(code, resolveLocale());
    }

    public static String getMessage(String code, Object... args) {
        return getMessage(code, resolveLocale(), args);
    }
}
