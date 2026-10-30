package com.example.MyShop_API.config;

import com.example.MyShop_API.exception.MessageHandlerUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;

@Configuration
public class MessageConfig {
    @Bean
    public MessageSource messageSource() {
        return MessageHandlerUtils.getMessageSource();
    }

    @Bean
    public LocaleResolver localeResolver() {
        return new AcceptHeaderLocaleResolver() {
            @Override
            public Locale resolveLocale(HttpServletRequest request) {
                return MessageHandlerUtils.resolveLocale(request);
            }
        };
    }
}
