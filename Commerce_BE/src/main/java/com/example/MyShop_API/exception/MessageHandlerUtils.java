package com.example.MyShop_API.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.Locale;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageHandlerUtils {
    private static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("vi");
    private static final ResourceBundleMessageSource SOURCE = createSource();

    private static ResourceBundleMessageSource createSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("messages");
        source.setDefaultEncoding("UTF-8");
        source.setFallbackToSystemLocale(false);
        return source;
    }

    public static MessageSource getMessageSource() {
        return SOURCE;
    }

    public static Locale resolveLocale(HttpServletRequest request) {
        String header = request == null ? null : request.getHeader("locale");
        if (header == null || header.isBlank()) return DEFAULT_LOCALE;
        Locale locale = Locale.forLanguageTag(header.trim());
        return "en".equals(locale.getLanguage()) ? Locale.ENGLISH : DEFAULT_LOCALE;
    }

    public static Locale resolveLocale() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
            return resolveLocale(attrs.getRequest());
        }
        return DEFAULT_LOCALE;
    }

    public static String getMessage(String code, Locale locale, Object... args) {
        Locale supported = locale != null && "en".equals(locale.getLanguage()) ? Locale.ENGLISH : DEFAULT_LOCALE;
        return SOURCE.getMessage(code, args, code, supported);
    }

    public static String getMessage(String code) {
        return getMessage(code, resolveLocale());
    }

    public static String getMessage(String code, Object... args) {
        return getMessage(code, resolveLocale(), args);
    }
}
