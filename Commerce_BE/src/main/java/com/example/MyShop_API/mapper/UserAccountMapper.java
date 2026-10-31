package com.example.MyShop_API.mapper;

import com.example.MyShop_API.entity.*;
import com.example.MyShop_API.dto.request.*;
import com.example.MyShop_API.dto.response.*;
import com.example.MyShop_API.service.model.ModelLookup;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserAccountMapper {
    @Autowired protected ModelLookup models;
    @Autowired protected UserProfileMapper profiles;
    @Autowired protected RoleMapper roles;
    @Mapping(source = "password", target = "passwordHash")
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
        response.setUserProfile(profiles.toResponse(models.profileForUser(user.getId())));
        response.setRoles(models.roles(user.getId()).stream().map(roles::toResponse).collect(java.util.stream.Collectors.toSet()));
    }
}
