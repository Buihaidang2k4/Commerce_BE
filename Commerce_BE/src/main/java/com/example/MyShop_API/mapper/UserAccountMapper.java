package com.example.MyShop_API.mapper;

import com.example.MyShop_API.constants.AuthConstants;

import com.example.MyShop_API.repo.RoleRepository;
import com.example.MyShop_API.repo.UserProfileRepository;
import com.example.MyShop_API.repo.PermissionRepository;
import java.util.stream.Collectors;
import java.util.Optional;

import com.example.MyShop_API.entity.*;
import com.example.MyShop_API.dto.request.*;
import com.example.MyShop_API.dto.response.*;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserAccountMapper {
    @Autowired protected RoleRepository roleRepository;
    @Autowired protected PermissionRepository permissionRepository;
    @Autowired protected UserProfileRepository userProfileRepository;
    @Autowired protected UserProfileMapper profiles;
    @Autowired protected RoleMapper roles;
    @Mapping(target = "passwordHash", ignore = true)
    public abstract UserAccount toEntity(UserCreationRequest request);
    @Mapping(source = "lockReason", target = "lockedReason")
    public abstract UserResponse toResponse(UserAccount user);
    @Mapping(source = "lockedReason", target = "lockReason")
    public abstract UserAccount toEntity(UserResponse response);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    public abstract void update(UserUpdateRequest request, @MappingTarget UserAccount user);

    @AfterMapping
    protected void fill(UserAccount user, @MappingTarget UserResponse response) {
        response.setUserProfile(profiles.toResponse(Optional.ofNullable(user.getId()).flatMap(userProfileRepository::findByUserId).orElse(null)));
        response.setRoles(roleRepository.findAllByUserId(user.getId()).stream().map(roles::toResponse).collect(java.util.stream.Collectors.toSet()));
        response.setPermissions(permissionRepository.findAllByUserId(user.getId()).stream()
                .map(Permission::getCode).filter(code -> code != null && !code.startsWith(AuthConstants.ROLE_PREFIX) && code.matches(AuthConstants.PERMISSION_CODE_PATTERN))
                .collect(Collectors.toSet()));
    }
}
