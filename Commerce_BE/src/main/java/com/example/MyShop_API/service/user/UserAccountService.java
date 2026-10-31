package com.example.MyShop_API.service.user;

import com.example.MyShop_API.utils.AuthUtils;

import com.example.MyShop_API.constants.RoleCodeConstants;
import com.example.MyShop_API.constants.AccountStatusConstants;

import com.example.MyShop_API.constants.CommonMessageConstants;
import com.example.MyShop_API.constants.PredefinedRoleConstants;
import com.example.MyShop_API.dto.request.ChangePasswordRequest;
import com.example.MyShop_API.dto.request.UserCreationRequest;
import com.example.MyShop_API.dto.request.UserUpdateRequest;
import com.example.MyShop_API.dto.response.UserResponse;
import com.example.MyShop_API.entity.Role;
import com.example.MyShop_API.entity.ShoppingCart;
import com.example.MyShop_API.entity.UserAccount;
import com.example.MyShop_API.entity.UserProfile;
import com.example.MyShop_API.entity.UserRole;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.mapper.UserAccountMapper;
import com.example.MyShop_API.repo.PasswordResetChallengeRepository;
import com.example.MyShop_API.repo.RefreshSessionRepository;
import com.example.MyShop_API.repo.RoleRepository;
import com.example.MyShop_API.repo.ShoppingCartRepository;
import com.example.MyShop_API.repo.UserAccountRepository;
import com.example.MyShop_API.repo.UserProfileRepository;
import com.example.MyShop_API.repo.UserRoleRepository;
import com.example.MyShop_API.service.userprofie.IUserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
public class UserAccountService implements IUserAccountService {
    private final UserProfileRepository userProfileRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserAccountRepository userRepository;
    private final UserAccountMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final IUserProfileService profileService;
    private final RefreshSessionRepository sessionRepository;
    private final PasswordResetChallengeRepository challengeRepository;

    @Transactional(readOnly = true)
    public List<UserResponse> getUsers() {
        AuthUtils.validateCheckAuth(List.of(RoleCodeConstants.ADMIN));
        return userRepository.findAll().stream().map(userMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        AuthUtils.validateCheckAuth(List.of(RoleCodeConstants.ADMIN));
        return userMapper.toResponse(userRepository.findById(id).orElseThrow(() -> new AppException(CommonMessageConstants.USER_NOT_EXISTED)));
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public UserAccount findUserByPrincipal(Principal principal) {
        return userRepository.findByEmail(principal.getName()).orElseThrow(() -> new AppException(CommonMessageConstants.USER_NOT_EXISTED));
    }

    @Transactional
    public UserResponse createUser(UserCreationRequest request) {
        // A stable parent row serializes registration across application instances without UNIQUE/FK.
        Role role = roleRepository.findByCodeForUpdate(PredefinedRoleConstants.USER_ROLE)
                .orElseThrow(() -> new AppException(CommonMessageConstants.AUTH_CONFIGURATION));
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmailIgnoreCase(email)) throw new AppException(CommonMessageConstants.USER_EXISTED);
        UserAccount user = UserAccount.builder().email(email).passwordHash(passwordEncoder.encode(request.getPassword())).accountStatus(AccountStatusConstants.ACTIVE).build();
        userRepository.saveAndFlush(user);
        replaceRoles(user.getId(), List.of(role));
        UserProfile profile = profileService.createEmptyUserProfile();
        profile.setUserId(user.getId());
        userProfileRepository.save(profile);
        ShoppingCart cart = shoppingCartRepository.findByUserProfileId(profile.getId())
                .orElseThrow(() -> new AppException(CommonMessageConstants.CART_NOT_EXISTED));
        cart.setUserId(user.getId());
        shoppingCartRepository.save(cart);
        return userMapper.toResponse(user);
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public void changePassword(ChangePasswordRequest request, Long id) {
        UserAccount user = lockAccount(id);
        requireOwner(user);
        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash()))
            throw new AppException(CommonMessageConstants.PASSWORD_NOT_MATCHES);
        if (!request.getNewPassword().equals(request.getConfirmPassword()))
            throw new AppException(CommonMessageConstants.PASSWORD_CONFIRM_NOT_MATCHES);
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        challengeRepository.deleteAllByUserId(id);
        sessionRepository.revokeAllByUserId(id, Instant.now());
    }

    @Transactional
    public UserResponse updateRoleUser(UserUpdateRequest request, Long id) {
        AuthUtils.validateCheckAuth(List.of(RoleCodeConstants.ADMIN));
        lockAdminCatalog();
        UserAccount user = lockAccount(id);
        if (user.getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getName()))
            throw new AppException(CommonMessageConstants.FORBIDDEN);
        if (AccountStatusConstants.DELETED.equals(user.getAccountStatus()))
            throw new AppException(CommonMessageConstants.USER_NOT_EXISTED);
        if (request.getRoles() == null || request.getRoles().isEmpty())
            throw new AppException(CommonMessageConstants.ROLE_INVALID);
        TreeSet<String> codes = new TreeSet<>();
        request.getRoles().forEach(code -> {
            if (code == null || code.isBlank()) throw new AppException(CommonMessageConstants.ROLE_INVALID);
            codes.add(code.trim().toUpperCase(Locale.ROOT));
        });
        List<Role> roles = roleRepository.findByCodeIn(codes);
        if (roles.size() != codes.size()) throw new AppException(CommonMessageConstants.ROLE_NOT_FOUND);
        replaceRoles(id, roles);
        sessionRepository.revokeAllByUserId(id, Instant.now());
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void lockUser(Long userId, String reason, Principal principal) {
        AuthUtils.validateCheckAuth(List.of(RoleCodeConstants.ADMIN));
        lockAdminCatalog();
        if (reason == null || reason.isBlank()) throw new AppException(CommonMessageConstants.LOCK_REASON_REQUIRED);
        UserAccount user = lockAccount(userId);
        if (user.getEmail().equals(principal.getName()))
            throw new AppException(CommonMessageConstants.CANNOT_LOCK_YOURSELF);
        protectAdmin(userId);
        if (AccountStatusConstants.DELETED.equals(user.getAccountStatus()))
            throw new AppException(CommonMessageConstants.USER_NOT_EXISTED);
        if (!user.isEnabled()) throw new AppException(CommonMessageConstants.USER_ALREADY_LOCKED);
        user.setAccountStatus(AccountStatusConstants.LOCKED);
        user.setLockReason(reason.trim());
        user.setLockedAt(Instant.now());
        userRepository.save(user);
        sessionRepository.revokeAllByUserId(userId, Instant.now());
    }

    @Override
    @Transactional
    public void unlockUser(Long userId) {
        AuthUtils.validateCheckAuth(List.of(RoleCodeConstants.ADMIN));
        lockAdminCatalog();
        UserAccount user = lockAccount(userId);
        if (AccountStatusConstants.DELETED.equals(user.getAccountStatus()))
            throw new AppException(CommonMessageConstants.USER_NOT_EXISTED);
        if (user.isEnabled()) throw new AppException(CommonMessageConstants.USER_ALREADY_UNLOCKED);
        user.setAccountStatus(AccountStatusConstants.ACTIVE);
        user.setLockReason(null);
        user.setLockedAt(null);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUserById(Long id) {
        AuthUtils.validateCheckAuth(List.of(RoleCodeConstants.ADMIN));
        lockAdminCatalog();
        UserAccount user = lockAccount(id);
        protectAdmin(id);
        user.setAccountStatus(AccountStatusConstants.DELETED);
        user.setLockedAt(Instant.now());
        userRepository.save(user);
        userRoleRepository.deleteByUserId(id);
        challengeRepository.deleteAllByUserId(id);
        sessionRepository.revokeAllByUserId(id, Instant.now());
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public UserResponse getMyInfor() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAccount user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(CommonMessageConstants.USER_NOT_EXISTED));
        if (!user.isEnabled()) throw new AppException(CommonMessageConstants.UNAUTHENTICATED);
        return userMapper.toResponse(user);
    }

    private void lockAdminCatalog() {
        roleRepository.findByCodeForUpdate(PredefinedRoleConstants.USER_ROLE)
                .orElseThrow(() -> new AppException(CommonMessageConstants.AUTH_CONFIGURATION));
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAccount actor = userRepository.findByEmail(email).filter(UserAccount::isEnabled)
                .orElseThrow(() -> new AppException(CommonMessageConstants.FORBIDDEN));
        if (roleRepository.findAllByUserId(actor.getId()).stream().noneMatch(role -> RoleCodeConstants.ADMIN.equals(role.getCode())))
            throw new AppException(CommonMessageConstants.FORBIDDEN);
    }

    private UserAccount lockAccount(Long id) {
        return userRepository.findByIdForUpdate(id).orElseThrow(() -> new AppException(CommonMessageConstants.USER_NOT_EXISTED));
    }

    private void requireOwner(UserAccount user) {
        if (!user.isEnabled() || !user.getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getName()))
            throw new AppException(CommonMessageConstants.FORBIDDEN);
    }

    private void protectAdmin(Long id) {
        if (roleRepository.findAllByUserId(id).stream().anyMatch(role -> RoleCodeConstants.ADMIN.equals(role.getCode())))
            throw new AppException(CommonMessageConstants.CANNOT_LOCK_ADMIN);
    }

    private void replaceRoles(Long userId, Collection<Role> roles) {
        userRoleRepository.deleteByUserId(userId);
        userRoleRepository.flush();
        userRoleRepository.saveAll(roles.stream().map(role -> UserRole.builder().userId(userId).roleId(role.getId()).build()).toList());
    }
}
