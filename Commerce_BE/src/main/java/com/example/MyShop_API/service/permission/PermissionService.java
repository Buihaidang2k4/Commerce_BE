package com.example.MyShop_API.service.permission;

import com.example.MyShop_API.utils.AuthUtils;

import com.example.MyShop_API.constants.RoleCodeConstants;
import com.example.MyShop_API.constants.AuthConstants;

import com.example.MyShop_API.constants.CommonMessageConstants;
import com.example.MyShop_API.dto.request.PermissionRequest;
import com.example.MyShop_API.entity.Permission;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.repo.PermissionRepository;
import com.example.MyShop_API.repo.RolePermissionRepository;
import com.example.MyShop_API.repo.RoleRepository;
import lombok.RequiredArgsConstructor;
import com.example.MyShop_API.entity.UserAccount;
import com.example.MyShop_API.repo.UserAccountRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final RoleRepository roleRepository;
    private final UserAccountRepository userRepository;

    @Transactional(readOnly = true)
    public List<Permission> getAll() {
        AuthUtils.validateCheckAuth(List.of(RoleCodeConstants.ADMIN));
        return permissionRepository.findAll();
    }

    @Transactional
    public Permission create(PermissionRequest request) {
        AuthUtils.validateCheckAuth(List.of(RoleCodeConstants.ADMIN));
        lockCatalog();
        String code = normalizeCode(request.getCode());
        if (permissionRepository.findByCode(code).isPresent()) throw new AppException(CommonMessageConstants.PERMISSION_EXISTED);
        Permission permission = Permission.builder().code(code).build();
        apply(request, permission);
        return permissionRepository.save(permission);
    }

    @Transactional
    public Permission update(Long id, PermissionRequest request) {
        AuthUtils.validateCheckAuth(List.of(RoleCodeConstants.ADMIN));
        lockCatalog();
        Permission permission = permissionRepository.findById(id).orElseThrow(() -> new AppException(CommonMessageConstants.PERMISSION_NOT_FOUND));
        if (!permission.getCode().equals(normalizeCode(request.getCode()))) throw new AppException(CommonMessageConstants.PERMISSION_INVALID);
        apply(request, permission);
        return permissionRepository.save(permission);
    }

    @Transactional
    public void delete(Long id) {
        AuthUtils.validateCheckAuth(List.of(RoleCodeConstants.ADMIN));
        lockCatalog();
        Permission permission = permissionRepository.findById(id).orElseThrow(() -> new AppException(CommonMessageConstants.PERMISSION_NOT_FOUND));
        rolePermissionRepository.deleteByPermissionId(id);
        permissionRepository.delete(permission);
    }

    private void lockCatalog() {
        roleRepository.findByCodeForUpdate(RoleCodeConstants.USER).orElseThrow(() -> new AppException(CommonMessageConstants.AUTH_CONFIGURATION));
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAccount actor = userRepository.findByEmail(email).filter(UserAccount::isEnabled)
                .orElseThrow(() -> new AppException(CommonMessageConstants.FORBIDDEN));
        if (roleRepository.findAllByUserId(actor.getId()).stream().noneMatch(role -> RoleCodeConstants.ADMIN.equals(role.getCode())))
            throw new AppException(CommonMessageConstants.FORBIDDEN);
    }

    private String normalizeCode(String code) {
        if (code == null) throw new AppException(CommonMessageConstants.PERMISSION_INVALID);
        String value = code.trim().toUpperCase(Locale.ROOT);
        if (!value.matches(AuthConstants.PERMISSION_CODE_PATTERN) || value.startsWith(AuthConstants.ROLE_PREFIX)) throw new AppException(CommonMessageConstants.PERMISSION_INVALID);
        return value;
    }

    private void apply(PermissionRequest request, Permission permission) {
        permission.setResourceCode(request.getResourceCode());
        permission.setActionCode(request.getActionCode());
        permission.setScopeCode(request.getScopeCode());
        permission.setDescription(request.getDescription());
    }
}
