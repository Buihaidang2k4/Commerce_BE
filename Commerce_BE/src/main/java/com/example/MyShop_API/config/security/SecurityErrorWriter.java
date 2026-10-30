package com.example.MyShop_API.config.security;

import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.exception.MessageHandlerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityErrorWriter {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void write(HttpServletRequest request, HttpServletResponse response,
                             int status, int code, String messageKey) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        MAPPER.writeValue(response.getWriter(), ApiResponse.error(code,
                MessageHandlerUtils.getMessage(messageKey, MessageHandlerUtils.resolveLocale(request))));
    }
}
