package com.example.MyShop_API.service.authentication;

import com.example.MyShop_API.dto.request.AuthenticationRequest;
import com.example.MyShop_API.dto.request.IntrospectRequest;
import com.example.MyShop_API.dto.response.AuthenticationResponse;
import com.example.MyShop_API.dto.response.IntrospectResponse;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;

public interface IAuthenticationService {

    AuthenticationResponse authenticate(AuthenticationRequest request) throws ParseException;

    AuthenticationResponse authenticateGoogle(IntrospectRequest request) throws GeneralSecurityException, IOException, ParseException;

    IntrospectResponse introspect(IntrospectRequest request) throws ParseException, JOSEException;

    void logout(String refreshToken) throws ParseException, JOSEException;

    String refreshToken(String refreshToken) throws ParseException, JOSEException;

    ResponseCookie buildCookie(String refreshToken, String cookieName);

    ResponseCookie clearCookie(String cookieName);

    String getTokenFromCookie(HttpServletRequest request, String cookieName);

    JWTClaimsSet decodeToken(String token) throws ParseException;
}
