package com.example.MyShop_API.config.security;

import com.example.MyShop_API.constants.SecurityWhitelistConstants;
import com.example.MyShop_API.constants.CookieConstants;
import com.example.MyShop_API.constants.RedisConstants;

import com.example.MyShop_API.constants.ApiPathConstants;
import com.example.MyShop_API.constants.CommonMessageConstants;
import com.example.MyShop_API.constants.ErrorCatalogConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.util.Arrays;

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
import java.util.Locale;
import java.util.Set;
import jakarta.servlet.DispatcherType;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * Allow or block requests
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SecurityConfig {

    @NonFinal
    @Value("${app.cors.allowed-origins}")
    String allowedOrigins;

    @Autowired
    CustomJwtDecoder customJwtDecoder;

    @Bean
    public SecurityFilterChain securityWebFilterChain(HttpSecurity http, StringRedisTemplate redis,
            @Value("${api.prefix}") String apiPrefix,
            @Value("${app.auth.rate-limit.requests:20}") int authRequests,
            @Value("${app.auth.rate-limit.window-seconds:60}") int authWindow,
            @Value("${app.auth.rate-limit.store:redis}") String authRateStore) throws Exception {
        if (authRequests < 1 || authWindow < 1) throw new IllegalArgumentException("Invalid authentication rate limit");
        String rateStore = authRateStore.trim().toLowerCase(Locale.ROOT);
        if (!Set.of(RedisConstants.AUTH_RATE_STORE_REDIS, RedisConstants.AUTH_RATE_STORE_MEMORY).contains(rateStore))
            throw new IllegalArgumentException("Invalid authentication rate limit store");
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .requestCache(cache -> cache.disable())
                // CookieOriginFilter enforces an explicit browser origin for cookie writes.
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(new CookieOriginFilter(Arrays.stream(allowedOrigins.split(",")).map(String::trim).filter(v -> !v.isEmpty()).toList()), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new AuthRateLimitFilter(redis, apiPrefix, authRequests, authWindow, rateStore), CookieOriginFilter.class)
                .exceptionHandling(errors -> errors.accessDeniedHandler((request, response, exception) -> {
                    var definition = ErrorCatalogConstants.get(CommonMessageConstants.FORBIDDEN);
                    SecurityErrorWriter.write(request, response, definition.status().value(), definition.code(), CommonMessageConstants.FORBIDDEN);
                }))
                .httpBasic(httpBasicAuth -> httpBasicAuth.disable())
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers(ApiPathConstants.HEALTH, ApiPathConstants.HEALTH_ALL).permitAll()
                        .requestMatchers(SecurityWhitelistConstants.PUBLIC_ENDPOINTS.stream().map(path -> ApiPathConstants.resolve(apiPrefix, path)).toArray(String[]::new)).permitAll()
                        .requestMatchers(SecurityWhitelistConstants.SWAGGER_ENDPOINTS.toArray(new String[0])).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .bearerTokenResolver(new CookieBearerTokenResolver(CookieConstants.ACCESS_COOKIE_NAME, apiPrefix))
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
        configuration.setAllowedOrigins(Arrays.stream(allowedOrigins.split(",")).map(String::trim).filter(v -> !v.isEmpty()).toList());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
