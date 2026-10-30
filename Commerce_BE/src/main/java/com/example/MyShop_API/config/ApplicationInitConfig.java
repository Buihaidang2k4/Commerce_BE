package com.example.MyShop_API.config;

import com.example.MyShop_API.entity.Address;
import com.example.MyShop_API.entity.Cart;
import com.example.MyShop_API.entity.Role;
import com.example.MyShop_API.entity.User;
import com.example.MyShop_API.repo.AddressRepository;
import com.example.MyShop_API.repo.RoleRepository;
import com.example.MyShop_API.repo.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;

@Configuration
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {
    private final com.example.MyShop_API.service.model.ModelLookup models;
    @Autowired
    @Lazy
    PasswordEncoder passwordEncoder;

    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "app.bootstrap-admin.enabled", havingValue = "true")
    @Bean
    ApplicationRunner initApplicationRunner(UserRepository userRepository, RoleRepository roleRepository,
            @org.springframework.beans.factory.annotation.Value("${BOOTSTRAP_ADMIN_EMAIL}") String adminEmail,
            @org.springframework.beans.factory.annotation.Value("${BOOTSTRAP_ADMIN_PASSWORD}") String adminPassword) {
        if (adminEmail.isBlank() || adminPassword.length() < 12) {
            throw new IllegalArgumentException("Bootstrap admin needs email and a password of at least 12 characters");
        }

        return args -> {
            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                Role role = roleRepository.findById("ADMIN").orElseGet(
                        () -> roleRepository.save(
                                Role.builder()
                                        .roleName("ADMIN")
                                        .description("Default role")
                                        .build()
                        )
                );

                roleRepository.save(role);

                User user = User.builder()
//                        .username("admin")
                        .email(adminEmail)
                        .password(passwordEncoder.encode(adminPassword))
                        .enabled(true)
                        .build();


                user = userRepository.save(user);
                models.replaceRoles(user.getId(), List.of(role));
            }
        };
    }
}
