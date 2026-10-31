package com.example.MyShop_API.service.authentication;

import com.example.MyShop_API.dto.request.AuthenticationRequest;
import com.example.MyShop_API.dto.request.IntrospectRequest;
import com.example.MyShop_API.dto.response.AuthenticationResponse;
import com.example.MyShop_API.dto.response.IntrospectResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import java.time.Instant;

public interface IAuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request);
    AuthenticationResponse refreshToken(String refreshToken);
    IntrospectResponse introspect(IntrospectRequest request);
    void logout(String refreshToken, String accessToken);
    ResponseCookie buildCookie(String token, String cookieName, Instant expiresAt);
    ResponseCookie clearCookie(String cookieName);
    String getTokenFromCookie(HttpServletRequest request, String cookieName);
}
