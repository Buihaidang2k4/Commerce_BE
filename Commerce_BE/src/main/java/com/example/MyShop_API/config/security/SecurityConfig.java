package com.example.MyShop_API.config.security;

import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Allow or block requests
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SecurityConfig {
    private static final String ACCESS_TOKEN_NAME = "access_token";

    @NonFinal
    @Value("${app.cors.allowed-origins}")
    String allowedOrigins;

    @Autowired
    CustomJwtDecoder customJwtDecoder;

    @Bean
    public SecurityFilterChain securityWebFilterChain(HttpSecurity http, org.springframework.data.redis.core.StringRedisTemplate redis,
            @Value("${api.prefix}") String apiPrefix,
            @Value("${app.auth.rate-limit.requests:20}") int authRequests,
            @Value("${app.auth.rate-limit.window-seconds:60}") int authWindow) throws Exception {
        if (authRequests < 1 || authWindow < 1) throw new IllegalArgumentException("Invalid authentication rate limit");
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // CookieOriginFilter enforces an explicit browser origin for cookie writes.
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(new CookieOriginFilter(java.util.Arrays.stream(allowedOrigins.split(",")).map(String::trim).filter(v -> !v.isEmpty()).toList()), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new AuthRateLimitFilter(redis, apiPrefix, authRequests, authWindow), CookieOriginFilter.class)
                .exceptionHandling(errors -> errors.accessDeniedHandler((request, response, exception) -> {
                    var definition = com.example.MyShop_API.exception.ErrorCatalog.get(com.example.MyShop_API.constants.CommonMessageUtils.FORBIDDEN);
                    SecurityErrorWriter.write(request, response, definition.status().value(), definition.code(), com.example.MyShop_API.constants.CommonMessageUtils.FORBIDDEN);
                }))
                .httpBasic(httpBasicAuth -> httpBasicAuth.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(com.example.MyShop_API.constants.ApiPath.HEALTH, com.example.MyShop_API.constants.ApiPath.HEALTH_ALL).permitAll()
                        .requestMatchers(SecurityWhitelist.PUBLIC_ENDPOINTS.stream().map(path -> com.example.MyShop_API.constants.ApiPath.resolve(apiPrefix, path)).toArray(String[]::new)).permitAll()
                        .requestMatchers(SecurityWhitelist.SWAGGER_ENDPOINTS.toArray(new String[0])).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .bearerTokenResolver(new CookieBearerTokenResolver(ACCESS_TOKEN_NAME))
                        .jwt(jwtConfigurer -> jwtConfigurer
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                                .decoder(customJwtDecoder))
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                );
        return http.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(java.util.Arrays.stream(allowedOrigins.split(",")).map(String::trim).filter(v -> !v.isEmpty()).toList());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
