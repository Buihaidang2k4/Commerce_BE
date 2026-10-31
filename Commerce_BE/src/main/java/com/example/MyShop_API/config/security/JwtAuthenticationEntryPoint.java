package com.example.MyShop_API.config.security;

import com.example.MyShop_API.constants.CommonMessageConstants;
import com.example.MyShop_API.constants.ErrorCatalogConstants;
import com.example.MyShop_API.exception.MessageHandlerUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import java.io.IOException;

@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException exception) throws IOException {
        String key = CommonMessageConstants.UNAUTHENTICATED;
        var definition = ErrorCatalogConstants.get(key);
        log.warn(MessageHandlerUtils.getMessage(key, MessageHandlerUtils.resolveLocale(request)));
        SecurityErrorWriter.write(request, response, definition.status().value(), definition.code(), key);
    }
}
