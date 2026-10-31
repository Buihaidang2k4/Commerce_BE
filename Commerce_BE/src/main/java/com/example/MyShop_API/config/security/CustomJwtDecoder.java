package com.example.MyShop_API.config.security;

import com.example.MyShop_API.constants.CommonMessageConstants;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.exception.MessageHandlerUtils;
import com.example.MyShop_API.service.authentication.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomJwtDecoder implements JwtDecoder {
    private final AuthenticationService authenticationService;
    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            return authenticationService.validateAccessToken(token);
        } catch (AppException ex) {
            throw new BadJwtException(MessageHandlerUtils.getMessage(CommonMessageConstants.UNAUTHENTICATED));
        }
    }
}
