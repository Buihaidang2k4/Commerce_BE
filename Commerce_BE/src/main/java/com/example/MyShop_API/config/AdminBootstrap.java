package com.example.MyShop_API.config;

import com.example.MyShop_API.constants.RoleCodeConstants;

import com.example.MyShop_API.dto.request.UserCreationRequest;
import com.example.MyShop_API.entity.Role;
import com.example.MyShop_API.entity.UserRole;
import com.example.MyShop_API.repo.RoleRepository;
import com.example.MyShop_API.repo.UserAccountRepository;
import com.example.MyShop_API.repo.UserRoleRepository;
import com.example.MyShop_API.service.user.IUserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Locale;

/** Explicit opt-in only; never resets passwords or promotes an existing customer. */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.bootstrap-admin.enabled", havingValue = "true")
public class AdminBootstrap implements ApplicationRunner {
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserAccountRepository userRepository;
    private final IUserAccountService userService;

    @Value("${BOOTSTRAP_ADMIN_EMAIL:}") private String email;
    @Value("${BOOTSTRAP_ADMIN_PASSWORD:}") private String password;

    @Override
    @Transactional
    public void run(ApplicationArguments arguments) {
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        if (!normalized.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+") || password.length() < 12 || password.length() > 72
                || !password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$"))
            throw new IllegalStateException("Configure a valid bootstrap email and a 12-72 character strong password");
        roleRepository.findByCodeForUpdate(RoleCodeConstants.USER).orElseThrow(() -> new IllegalStateException("Run auth migration before admin bootstrap"));
        Role admin = roleRepository.findByCode(RoleCodeConstants.ADMIN).orElseThrow(() -> new IllegalStateException("Missing ADMIN role"));
        var existing = userRepository.findByEmailIgnoreCase(normalized);
        if (existing.isPresent()) {
            boolean isAdmin = roleRepository.findAllByUserId(existing.get().getId()).stream().anyMatch(role -> RoleCodeConstants.ADMIN.equals(role.getCode()));
            if (!isAdmin) throw new IllegalStateException("Bootstrap email belongs to an existing customer; choose another email");
            return;
        }
        var created = userService.createUser(UserCreationRequest.builder().email(normalized).password(password).build());
        userRoleRepository.save(UserRole.builder().userId(created.getId()).roleId(admin.getId()).build());
    }
}
