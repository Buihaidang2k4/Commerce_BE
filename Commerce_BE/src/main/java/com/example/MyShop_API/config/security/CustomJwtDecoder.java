package com.example.MyShop_API.config.security;

import com.example.MyShop_API.dto.request.IntrospectRequest;
import com.example.MyShop_API.service.authentication.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;

@Component
@Slf4j
public class CustomJwtDecoder implements JwtDecoder {

    @Autowired
    private AuthenticationService authenticationService;

    @Value("${jwt.secret}")
    private String signerKey;

    private NimbusJwtDecoder jwtDecoder;

    @PostConstruct
    public void initDecoder() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HmacSHA256");
        this.jwtDecoder = NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            var response = authenticationService.introspect(
                    IntrospectRequest.builder().token(token).build()
            );

            if (!response.isValid()) {
                throw new JwtException(com.example.MyShop_API.exception.MessageHandlerUtils.getMessage(com.example.MyShop_API.constants.CommonMessageUtils.TOKEN_EXPIRED));
            }

            return jwtDecoder.decode(token);

        } catch (ParseException | JOSEException e) {
            log.warn(com.example.MyShop_API.exception.MessageHandlerUtils.getMessage(com.example.MyShop_API.constants.CommonMessageUtils.UNAUTHENTICATED));
            throw new JwtException(com.example.MyShop_API.exception.MessageHandlerUtils.getMessage(com.example.MyShop_API.constants.CommonMessageUtils.UNAUTHENTICATED), e);

        } catch (JwtException e) {
            log.warn(com.example.MyShop_API.exception.MessageHandlerUtils.getMessage(com.example.MyShop_API.constants.CommonMessageUtils.UNAUTHENTICATED));
            throw e;
        } catch (Exception e) {
            log.error(com.example.MyShop_API.exception.MessageHandlerUtils.getMessage(com.example.MyShop_API.constants.CommonMessageUtils.UNCATEGORIZED_EXCEPTION), e);
            throw new JwtException(com.example.MyShop_API.exception.MessageHandlerUtils.getMessage(com.example.MyShop_API.constants.CommonMessageUtils.UNAUTHENTICATED), e);
        }
    }
}