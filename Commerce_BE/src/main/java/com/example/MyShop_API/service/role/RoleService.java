package com.example.MyShop_API.service.role;

import com.example.MyShop_API.utils.AuthUtils;

import com.example.MyShop_API.constants.RoleCodeConstants;
import com.example.MyShop_API.constants.AuthConstants;

import com.example.MyShop_API.constants.CommonMessageConstants;
import com.example.MyShop_API.dto.request.RoleRequest;
import com.example.MyShop_API.dto.response.RoleResponse;
import com.example.MyShop_API.entity.Role;
import com.example.MyShop_API.entity.RolePermission;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.mapper.RoleMapper;
import com.example.MyShop_API.repo.RoleRepository;
import com.example.MyShop_API.repo.RolePermissionRepository;
import com.example.MyShop_API.repo.PermissionRepository;
import com.example.MyShop_API.repo.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import com.example.MyShop_API.entity.UserAccount;
import com.example.MyShop_API.repo.UserAccountRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {
    private final RoleRepository roleRepository;
    private final UserAccountRepository userRepository;
    private final RoleMapper roleMapper;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;
    private final UserRoleRepository userRoleRepository;

    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        AuthUtils.validateCheckAuth(List.of(RoleCodeConstants.ADMIN));
        return roleRepository.findAll().stream().map(this::response).toList();
    }

    @Transactional(readOnly = true)
    public RoleResponse getRoleById(String code) {
        AuthUtils.validateCheckAuth(List.of(RoleCodeConstants.ADMIN));
        return response(findRole(code));
    }

    @Transactional
    public RoleResponse createRole(RoleRequest request) {
        AuthUtils.validateCheckAuth(List.of(RoleCodeConstants.ADMIN));
        lockCatalog();
        String code = normalizeCode(request.getRoleName());
        if (roleRepository.findByCode(code).isPresent()) throw new AppException(CommonMessageConstants.ROLE_EXISTED);
        Role role = roleRepository.saveAndFlush(Role.builder().code(code).name(request.getName() == null ? code : request.getName().trim())
                .description(request.getDescription()).build());
        replacePermissions(role.getId(), request.getPermissionIds());
        return response(role);
    }

    @Transactional
    public RoleResponse updateRole(String code, RoleRequest request) {
        AuthUtils.validateCheckAuth(List.of(RoleCodeConstants.ADMIN));
        lockCatalog();
        Role role = findRole(code);
        // Stable role codes prevent accidental changes to security annotations and built-in roles.
        if (!role.getCode().equals(normalizeCode(request.getRoleName()))) throw new AppException(CommonMessageConstants.ROLE_INVALID);
        if (request.getName() != null) role.setName(request.getName().trim());
        if (request.getDescription() != null) role.setDescription(request.getDescription());
        roleRepository.save(role);
        replacePermissions(role.getId(), request.getPermissionIds());
        return response(role);
    }

    @Transactional
    public void deleteRole(String code) {
        AuthUtils.validateCheckAuth(List.of(RoleCodeConstants.ADMIN));
        lockCatalog();
        Role role = findRole(code);
        if (Set.of(RoleCodeConstants.USER, RoleCodeConstants.ADMIN).contains(role.getCode())) throw new AppException(CommonMessageConstants.FORBIDDEN);
        rolePermissionRepository.deleteByRoleId(role.getId());
        userRoleRepository.deleteByRoleId(role.getId());
        roleRepository.delete(role);
    }

    private Role findRole(String code) {
        return roleRepository.findByCode(normalizeCode(code)).orElseThrow(() -> new AppException(CommonMessageConstants.ROLE_NOT_FOUND));
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
        if (code == null) throw new AppException(CommonMessageConstants.ROLE_INVALID);
        String value = code.trim().toUpperCase(Locale.ROOT);
        if (!value.matches(AuthConstants.ROLE_CODE_PATTERN) || value.startsWith(AuthConstants.ROLE_PREFIX)) throw new AppException(CommonMessageConstants.ROLE_INVALID);
        return value;
    }

    private void replacePermissions(Long roleId, Set<Long> ids) {
        if (ids == null) return;
        if (permissionRepository.findAllById(ids).size() != ids.size()) throw new AppException(CommonMessageConstants.PERMISSION_NOT_FOUND);
        rolePermissionRepository.deleteByRoleId(roleId);
        rolePermissionRepository.flush();
        rolePermissionRepository.saveAll(ids.stream().map(id -> RolePermission.builder().roleId(roleId).permissionId(id).build()).toList());
    }

    private RoleResponse response(Role role) {
        RoleResponse response = roleMapper.toResponse(role);
        response.setPermissionIds(rolePermissionRepository.findByRoleId(role.getId()).stream().map(RolePermission::getPermissionId).distinct().sorted().toList());
        return response;
    }
}
