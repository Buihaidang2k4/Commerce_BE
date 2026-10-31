package com.example.MyShop_API.controller;

import com.example.MyShop_API.constants.CookieConstants;

import com.example.MyShop_API.constants.ApiPathConstants;
import com.example.MyShop_API.constants.CommonMessageConstants;
import com.example.MyShop_API.exception.AppException;

import com.example.MyShop_API.dto.request.AuthenticationRequest;
import com.example.MyShop_API.dto.request.ForgotPasswordRequest;
import com.example.MyShop_API.dto.request.IntrospectRequest;
import com.example.MyShop_API.dto.request.ResetPasswordRequest;
import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.dto.response.AuthenticationResponse;
import com.example.MyShop_API.dto.response.IntrospectResponse;
import com.example.MyShop_API.service.authentication.IAuthenticationService;
import com.example.MyShop_API.service.password_reset_otp.IPasswordResetChallengeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping(ApiPathConstants.BASE_AUTH)
public class AuthenticationController {

    IAuthenticationService authenticationService;
    IPasswordResetChallengeService resetOtpService;

    @ModelAttribute
    void preventCaching(HttpServletResponse response) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        response.setHeader(HttpHeaders.PRAGMA, "no-cache");
    }

    @PostMapping(ApiPathConstants.AUTH_LOGIN)
    ResponseEntity<ApiResponse> login(@Valid @RequestBody AuthenticationRequest request,
                                      HttpServletResponse response) {
        log.info("Login");
        AuthenticationResponse tokens = authenticationService.authenticate(request);
        ResponseCookie cookieAccess = authenticationService.buildCookie(tokens.getAccessToken(), CookieConstants.ACCESS_COOKIE_NAME, tokens.getExp());
        ResponseCookie cookieRefresh = authenticationService.buildCookie(tokens.getRefreshToken(), CookieConstants.REFRESH_COOKIE_NAME, tokens.getRefreshExp());

        response.addHeader(HttpHeaders.SET_COOKIE, cookieAccess.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, cookieRefresh.toString());


        return ResponseEntity.ok(new ApiResponse(200, "Login successfull", tokens.getExp()));
    }

    @PostMapping(ApiPathConstants.AUTH_REFRESH)
    ResponseEntity<?> refreshToken(HttpServletRequest request) {
        log.info("Refresh Token");

        String refreshToken = authenticationService.getTokenFromCookie(request, CookieConstants.REFRESH_COOKIE_NAME);
        if (refreshToken == null) throw new AppException(CommonMessageConstants.UNAUTHENTICATED);

        AuthenticationResponse tokens = authenticationService.refreshToken(refreshToken);
        ResponseCookie newAccessCookie = authenticationService.buildCookie(tokens.getAccessToken(), CookieConstants.ACCESS_COOKIE_NAME, tokens.getExp());
        ResponseCookie newRefreshCookie = authenticationService.buildCookie(tokens.getRefreshToken(), CookieConstants.REFRESH_COOKIE_NAME, tokens.getRefreshExp());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, newAccessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, newRefreshCookie.toString())
                .body(new ApiResponse(200, "Access token refreshed", tokens.getExp()));
    }

    @PostMapping(ApiPathConstants.AUTH_INTROSPECT)
    ResponseEntity<ApiResponse<IntrospectResponse>> introspect(HttpServletRequest request
    ) {
        String accessToken = authenticationService.getTokenFromCookie(request, CookieConstants.ACCESS_COOKIE_NAME);

        if (accessToken == null) {
            return ResponseEntity.ok(ApiResponse.<IntrospectResponse>builder().data(IntrospectResponse
                    .builder()
                    .valid(false)
                    .build()).build());
        }

        IntrospectResponse response = authenticationService
                .introspect(IntrospectRequest.builder()
                        .token(accessToken).build());


        return ResponseEntity.ok(new ApiResponse<>(200, "Introspect successful", response));
    }

    @PostMapping(ApiPathConstants.AUTH_LOGOUT)
    ResponseEntity<ApiResponse> logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("Logout");
        String refreshToken = authenticationService.getTokenFromCookie(request, CookieConstants.REFRESH_COOKIE_NAME);

        try {
            authenticationService.logout(refreshToken, authenticationService.getTokenFromCookie(request, CookieConstants.ACCESS_COOKIE_NAME));
        } finally {
            response.addHeader(HttpHeaders.SET_COOKIE, authenticationService.clearCookie(CookieConstants.ACCESS_COOKIE_NAME).toString());
            response.addHeader(HttpHeaders.SET_COOKIE, authenticationService.clearCookie(CookieConstants.REFRESH_COOKIE_NAME).toString());
        }

        return ResponseEntity.ok(new ApiResponse(200, "Logout successful", null));
    }

    @PostMapping(ApiPathConstants.AUTH_FORGOT_PASSWORD)
    ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        resetOtpService.sendOtp(request.getEmail());
        return ResponseEntity.ok(new ApiResponse(200, "OTP has been sent to your email", true));
    }

    @PutMapping(ApiPathConstants.AUTH_RESET_PASSWORD)
    ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        resetOtpService.resetPassword(request);
        return ResponseEntity.ok(new ApiResponse(200, "Password reset successful! You can log in now", true));
    }
}
