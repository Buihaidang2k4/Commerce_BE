package com.example.MyShop_API.service.authentication;

import com.example.MyShop_API.constants.AuthConstants;
import com.example.MyShop_API.constants.CookieConstants;
import com.example.MyShop_API.constants.CommonConstants;

import com.example.MyShop_API.constants.CommonMessageConstants;
import com.example.MyShop_API.dto.request.AuthenticationRequest;
import com.example.MyShop_API.dto.request.IntrospectRequest;
import com.example.MyShop_API.dto.response.AuthenticationResponse;
import com.example.MyShop_API.dto.response.IntrospectResponse;
import com.example.MyShop_API.entity.RefreshSession;
import com.example.MyShop_API.entity.UserAccount;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.repo.PermissionRepository;
import com.example.MyShop_API.repo.RefreshSessionRepository;
import com.example.MyShop_API.repo.RoleRepository;
import com.example.MyShop_API.repo.UserAccountRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final UserAccountRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RefreshSessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}") private String tokenKey;
    @Value("${jwt.expiration}") private long accessDuration;
    @Value("${jwt.refreshable-duration}") private long refreshDuration;
    @Value("${jwt.issuer:" + AuthConstants.DEFAULT_ISSUER + "}") private String issuer;
    @Value("${jwt.audience:" + AuthConstants.DEFAULT_AUDIENCE + "}") private String audience;
    @Value("${app.cookie.secure}") private boolean cookieSecure;
    @Value("${app.cookie.same-site}") private String cookieSameSite;
    private String dummyPasswordHash;

    @PostConstruct
    void validateConfiguration() {
        if (tokenKey.getBytes(StandardCharsets.UTF_8).length < AuthConstants.MIN_SECRET_BYTES || accessDuration <= 0 || refreshDuration < accessDuration)
            throw new IllegalStateException("Invalid JWT key or token durations");
        if (!CookieConstants.SAME_SITE_VALUES.contains(cookieSameSite) || (CookieConstants.SAME_SITE_NONE.equals(cookieSameSite) && !cookieSecure))
            throw new IllegalStateException("Invalid authentication cookie configuration");
        dummyPasswordHash = passwordEncoder.encode(UUID.randomUUID().toString());
    }

    @Override
    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        UserAccount user = userRepository.findByEmailForUpdate(request.getEmail().trim().toLowerCase(Locale.ROOT))
                .orElse(null);
        if (user == null) {
            passwordEncoder.matches(request.getPassword(), dummyPasswordHash);
            throw new AppException(CommonMessageConstants.INVALID_CREDENTIALS);
        }
        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash()))
            throw new AppException(CommonMessageConstants.INVALID_CREDENTIALS);
        requireActive(user);
        // Preserve the existing single-login policy, now revoking access tokens as well.
        sessionRepository.revokeAllByUserId(user.getId(), Instant.now());
        return issueTokens(user, UUID.randomUUID().toString(), Instant.now().plusMillis(refreshDuration));
    }

    @Override
    @Transactional(noRollbackFor = AppException.class)
    public AuthenticationResponse refreshToken(String token) {
        RefreshSessionRepository.SessionOwner candidate = findRefreshSession(token);
        UserAccount user = userRepository.findByIdForUpdate(candidate.getUserId())
                .orElseThrow(() -> new AppException(CommonMessageConstants.UNAUTHENTICATED));
        requireActive(user);
        RefreshSession current = sessionRepository.findByIdForUpdate(candidate.getId())
                .orElseThrow(() -> new AppException(CommonMessageConstants.UNAUTHENTICATED));
        if (current.getRevokedAt() != null) {
            // Commit replay revocation even though the request returns 401.
            sessionRepository.revokeFamily(user.getId(), current.getTokenFamilyId(), Instant.now());
            throw new AppException(CommonMessageConstants.TOKEN_REVOKED);
        }
        if (current.getExpiresAt() == null || !current.getExpiresAt().isAfter(Instant.now()))
            throw new AppException(CommonMessageConstants.TOKEN_EXPIRED);
        AuthenticationResponse result = issueTokens(user, current.getTokenFamilyId(), current.getExpiresAt());
        RefreshSession replacement = sessionRepository.findByTokenHash(DigestUtils.sha256Hex(result.getRefreshToken())).orElseThrow();
        current.setRevokedAt(Instant.now());
        current.setReplacedBySessionId(replacement.getId());
        sessionRepository.save(current);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public IntrospectResponse introspect(IntrospectRequest request) {
        try {
            Jwt jwt = validateAccessToken(request.getToken());
            return IntrospectResponse.builder().valid(true).exp(jwt.getExpiresAt()).build();
        } catch (AppException ex) {
            return IntrospectResponse.builder().valid(false).build();
        }
    }

    @Transactional(readOnly = true)
    public Jwt validateAccessToken(String token) {
        JWTClaimsSet claims = verifyAccessClaims(token, false);
        try {
            Long userId = claims.getLongClaim(AuthConstants.USER_ID_CLAIM);
            Long sessionId = claims.getLongClaim(AuthConstants.SESSION_ID_CLAIM);
            if (userId == null || sessionId == null) throw new AppException(CommonMessageConstants.UNAUTHENTICATED);
            UserAccount user = userRepository.findById(userId)
                    .orElseThrow(() -> new AppException(CommonMessageConstants.UNAUTHENTICATED));
            requireActive(user);
            RefreshSession session = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new AppException(CommonMessageConstants.TOKEN_REVOKED));
            if (!Objects.equals(session.getUserId(), userId) || session.getRevokedAt() != null || session.getExpiresAt() == null
                    || !session.getExpiresAt().isAfter(Instant.now()) || !Objects.equals(user.getEmail(), claims.getSubject()))
                throw new AppException(CommonMessageConstants.TOKEN_REVOKED);
            // Read current grants: removing a role/permission takes effect on the next request.
            return Jwt.withTokenValue(token).header(AuthConstants.AUTHORIZATION_ALGORITHM_HEADER, JWSAlgorithm.HS256.getName()).header(AuthConstants.AUTHORIZATION_TYPE_HEADER, JOSEObjectType.JWT.toString())
                    .claims(values -> values.putAll(claims.getClaims()))
                    .issuedAt(claims.getIssueTime().toInstant()).expiresAt(claims.getExpirationTime().toInstant())
                    .claim(AuthConstants.SCOPE_CLAIM, buildScope(userId)).build();
        } catch (ParseException ex) {
            throw new AppException(CommonMessageConstants.UNAUTHENTICATED);
        }
    }

    @Override
    @Transactional
    public void logout(String refreshToken, String accessToken) {
        RefreshSession session = null;
        if (refreshToken != null && !refreshToken.isBlank() && refreshToken.length() <= AuthConstants.MAX_LOGOUT_TOKEN_LENGTH)
            session = sessionRepository.findByTokenHash(DigestUtils.sha256Hex(refreshToken)).orElse(null);
        if (session == null && accessToken != null && !accessToken.isBlank()) {
            try {
                JWTClaimsSet claims = verifyAccessClaims(accessToken, true);
                session = sessionRepository.findById(claims.getLongClaim(AuthConstants.SESSION_ID_CLAIM)).orElse(null);
                if (session != null && !Objects.equals(session.getUserId(), claims.getLongClaim(AuthConstants.USER_ID_CLAIM))) session = null;
            } catch (AppException | ParseException | IllegalArgumentException ignored) {
                // Logout is idempotent for missing, expired or malformed credentials.
            }
        }
        if (session != null && userRepository.findByIdForUpdate(session.getUserId()).isPresent())
            sessionRepository.revokeFamily(session.getUserId(), session.getTokenFamilyId(), Instant.now());
    }

    private RefreshSessionRepository.SessionOwner findRefreshSession(String token) {
        if (token == null || !token.matches(AuthConstants.REFRESH_TOKEN_PATTERN)) throw new AppException(CommonMessageConstants.UNAUTHENTICATED);
        return sessionRepository.findOwnerByTokenHash(DigestUtils.sha256Hex(token))
                .orElseThrow(() -> new AppException(CommonMessageConstants.UNAUTHENTICATED));
    }

    private AuthenticationResponse issueTokens(UserAccount user, String familyId, Instant refreshExpiresAt) {
        byte[] random = new byte[AuthConstants.REFRESH_TOKEN_BYTES];
        RANDOM.nextBytes(random);
        String refresh = Base64.getUrlEncoder().withoutPadding().encodeToString(random);
        RefreshSession session = sessionRepository.saveAndFlush(RefreshSession.builder().userId(user.getId())
                .tokenHash(DigestUtils.sha256Hex(refresh)).tokenFamilyId(familyId).expiresAt(refreshExpiresAt).build());
        Instant now = Instant.now();
        Instant accessExpiresAt = now.plusMillis(accessDuration);
        if (accessExpiresAt.isAfter(refreshExpiresAt)) accessExpiresAt = refreshExpiresAt;
        try {
            JWTClaimsSet claims = new JWTClaimsSet.Builder().subject(user.getEmail()).issuer(issuer).audience(audience)
                    .issueTime(Date.from(now)).expirationTime(Date.from(accessExpiresAt)).jwtID(UUID.randomUUID().toString())
                    .claim(AuthConstants.TOKEN_TYPE_CLAIM, AuthConstants.ACCESS_TOKEN_TYPE).claim(AuthConstants.USER_ID_CLAIM, user.getId()).claim(AuthConstants.SESSION_ID_CLAIM, session.getId())
                    .claim(AuthConstants.SCOPE_CLAIM, buildScope(user.getId())).build();
            SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build(), claims);
            jwt.sign(new MACSigner(tokenKey.getBytes(StandardCharsets.UTF_8)));
            return AuthenticationResponse.builder().accessToken(jwt.serialize()).refreshToken(refresh)
                    .exp(accessExpiresAt).refreshExp(refreshExpiresAt).build();
        } catch (JOSEException ex) {
            throw new IllegalStateException("Cannot sign access token", ex);
        }
    }

    private JWTClaimsSet verifyAccessClaims(String token, boolean allowExpired) {
        try {
            if (token == null || token.length() > AuthConstants.MAX_ACCESS_TOKEN_LENGTH) throw new AppException(CommonMessageConstants.UNAUTHENTICATED);
            SignedJWT jwt = SignedJWT.parse(token);
            if (!JWSAlgorithm.HS256.equals(jwt.getHeader().getAlgorithm()) || !JOSEObjectType.JWT.equals(jwt.getHeader().getType())
                    || !jwt.verify(new MACVerifier(tokenKey.getBytes(StandardCharsets.UTF_8))))
                throw new AppException(CommonMessageConstants.UNAUTHENTICATED);
            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            Instant now = Instant.now();
            if (!issuer.equals(claims.getIssuer()) || claims.getAudience() == null || !claims.getAudience().contains(audience)
                    || !AuthConstants.ACCESS_TOKEN_TYPE.equals(claims.getStringClaim(AuthConstants.TOKEN_TYPE_CLAIM)) || claims.getExpirationTime() == null
                    || claims.getIssueTime() == null || claims.getSubject() == null || claims.getJWTID() == null
                    || claims.getIssueTime().toInstant().isAfter(now.plusSeconds(AuthConstants.CLOCK_SKEW_SECONDS)))
                throw new AppException(CommonMessageConstants.UNAUTHENTICATED);
            if (!allowExpired && !claims.getExpirationTime().toInstant().isAfter(now))
                throw new AppException(CommonMessageConstants.TOKEN_EXPIRED);
            return claims;
        } catch (ParseException | JOSEException | IllegalArgumentException ex) {
            throw new AppException(CommonMessageConstants.UNAUTHENTICATED);
        }
    }

    private void requireActive(UserAccount user) {
        if (!user.isEnabled()) throw new AppException(CommonMessageConstants.UNAUTHENTICATED);
    }

    private String buildScope(Long userId) {
        TreeSet<String> authorities = new TreeSet<>();
        roleRepository.findAllByUserId(userId).forEach(role -> {
            if (role.getCode() != null && role.getCode().matches(AuthConstants.ROLE_CODE_PATTERN) && !role.getCode().startsWith(AuthConstants.ROLE_PREFIX))
                authorities.add(AuthConstants.ROLE_PREFIX + role.getCode());
        });
        permissionRepository.findAllByUserId(userId).forEach(permission -> {
            String code = permission.getCode();
            if (code != null && !code.startsWith(AuthConstants.ROLE_PREFIX) && code.matches(AuthConstants.PERMISSION_CODE_PATTERN)) authorities.add(code);
        });
        return String.join(" ", authorities);
    }

    @Scheduled(cron = AuthConstants.SESSION_CLEANUP_CRON, zone = CommonConstants.TIME_ZONE_UTC)
    @Transactional
    public void cleanupExpiredSessions() {
        // Keep expired chains for seven days; never delete a still-valid replay marker.
        sessionRepository.deleteExpiredBefore(Instant.now().minus(Duration.ofDays(AuthConstants.SESSION_RETENTION_DAYS)));
    }

    @Override
    public ResponseCookie clearCookie(String cookieName) {
        return ResponseCookie.from(cookieName, "").httpOnly(true).secure(cookieSecure).sameSite(cookieSameSite).path(CookieConstants.PATH).maxAge(0).build();
    }

    @Override
    public ResponseCookie buildCookie(String token, String cookieName, Instant expiresAt) {
        Duration remaining = Duration.between(Instant.now(), expiresAt);
        return ResponseCookie.from(cookieName, token).httpOnly(true).secure(cookieSecure).sameSite(cookieSameSite)
                .path("/").maxAge(remaining.isNegative() ? Duration.ZERO : remaining).build();
    }

    @Override
    public String getTokenFromCookie(HttpServletRequest request, String cookieName) {
        return Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(cookie -> cookieName.equals(cookie.getName())).map(Cookie::getValue).findFirst().orElse(null);
    }
}
