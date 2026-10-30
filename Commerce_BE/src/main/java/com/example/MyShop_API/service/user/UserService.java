package com.example.MyShop_API.service.user;

import com.example.MyShop_API.anotation.AdminOnly;
import com.example.MyShop_API.anotation.AllAccess;
import com.example.MyShop_API.constants.PredefinedRole;
import com.example.MyShop_API.dto.request.ChangePasswordRequest;
import com.example.MyShop_API.dto.request.UserCreationRequest;
import com.example.MyShop_API.dto.request.UserUpdateRequest;
import com.example.MyShop_API.dto.response.UserResponse;
import com.example.MyShop_API.entity.*;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.constants.CommonMessageUtils;
import com.example.MyShop_API.mapper.UserMapper;
import com.example.MyShop_API.repo.*;
import com.example.MyShop_API.service.userprofie.UserProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService implements IUserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    UserProfileService profileService;

    @AdminOnly
    @Transactional(readOnly = true)
    public List<UserResponse> getUsers() {
        log.info("getUsers().........");
        return userRepository.findAll().stream().map(userMapper::toResponse).toList();
    }

    @AdminOnly
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        return userMapper.toResponse(user);
    }

    @Override
    @AdminOnly
    @Transactional(readOnly = true)
    public User findUserByPrincipal(Principal principal) {
        return userRepository.findByEmail(principal.getName()).orElseThrow(() -> new AppException(CommonMessageUtils.USER_NOT_EXISTED));
    }


    @Transactional
    public UserResponse createUser(UserCreationRequest request) {
        log.info("createUser().........");
        User user = userMapper.toEntity(request);

        // convert pass
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);

        // set role defautlt nếu không có tạo mới
        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresentOrElse(roles::add,
                () -> {
                    Role defaultRole = Role.builder()
                            .roleName("USER")
                            .description("Role User default")
                            .build();
                    roleRepository.save(defaultRole);
                    roles.add(defaultRole);
                });

        user.setRoles(roles);

        // Tạo profile mặc định
        UserProfile profile = profileService.createEmptyUserProfile();
        profile.setUser(user);
        user.setProfile(profile);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(CommonMessageUtils.USER_EXISTED);
        }

        return userMapper.toResponse(user);
    }

    @AllAccess
    @Transactional
    public void changePassword(ChangePasswordRequest request, Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new AppException(CommonMessageUtils.USER_NOT_EXISTED));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(CommonMessageUtils.PASSWORD_NOT_MATCHES);
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(CommonMessageUtils.PASSWORD_CONFIRM_NOT_MATCHES);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        try {
            userRepository.save(user);
            log.info("================== CHANGE PASSWORD ===================");
        } catch (DataIntegrityViolationException e) {
            throw new AppException(CommonMessageUtils.USER_EXISTED);
        }
    }

    @AdminOnly
    @Transactional
    public UserResponse updateRoleUser(UserUpdateRequest request, Long id) {
        log.info("updateUser().........");
        User findUser = userRepository.findById(id)
                .orElseThrow(() -> new AppException(CommonMessageUtils.USER_NOT_EXISTED));
        userMapper.update(request, findUser);
        var roles = roleRepository.findAllById(request.getRoles());

        findUser.setRoles(new HashSet<>(roles));

        try {
            findUser = userRepository.save(findUser);

        } catch (DataIntegrityViolationException e) {
            throw new AppException(CommonMessageUtils.USER_EXISTED);
        }

        return userMapper.toResponse(findUser);
    }

    @Override
    @AdminOnly
    @Transactional
    public void lockUser(Long userId, String reason, Principal principal) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new AppException(CommonMessageUtils.LOCK_REASON_REQUIRED);
        }
        User current = findUserByPrincipal(principal);

        if (userId.equals(current.getId()))
            throw new AppException(CommonMessageUtils.CANNOT_LOCK_YOURSELF);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(CommonMessageUtils.USER_NOT_EXISTED));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals(com.example.MyShop_API.Enum.Role.ADMIN.name()));

        if (isAdmin)
            throw new AppException(CommonMessageUtils.CANNOT_LOCK_ADMIN);

        if (!user.isEnabled())
            throw new AppException(CommonMessageUtils.USER_ALREADY_LOCKED);

        user.setEnabled(false);
        user.setLockedReason(reason);
        user.setLockedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    @AdminOnly
    @Transactional
    public void unlockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(CommonMessageUtils.USER_NOT_EXISTED));

        if (user.isEnabled())
            throw new AppException(CommonMessageUtils.USER_ALREADY_UNLOCKED);
        user.setEnabled(true);
        user.setLockedReason("");

        userRepository.save(user);
    }

    @AdminOnly
    @Transactional
    public void deleteUserById(Long id) {
        log.info("deleteUserById().........");
        userRepository.deleteById(id);
    }

    @AllAccess // Tất cả role đều truy cập được
    @PostAuthorize("returnObject.email.toLowerCase() == authentication.name.toLowerCase()")
    public UserResponse getMyInfor() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(CommonMessageUtils.USER_NOT_EXISTED));

        log.info("user = {}", user.getProfile());
        UserResponse response = userMapper.toResponse(user);
        log.info("=========== Get Info ===============");
        return response;
    }
}
