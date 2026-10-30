package com.example.MyShop_API.mapper;

import com.example.MyShop_API.entity.*;
import com.example.MyShop_API.dto.request.*;
import com.example.MyShop_API.dto.response.*;
import com.example.MyShop_API.service.model.ModelLookup;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapper {
    @Autowired protected ModelLookup models;
    @Autowired protected UserProfileMapper profiles;
    @Autowired protected RoleMapper roles;
    @Mapping(target = "id", ignore = true)
    public abstract User toEntity(UserCreationRequest request);
    public abstract UserResponse toResponse(User user);
    public abstract User toEntity(UserResponse response);
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void update(UserUpdateRequest request, @MappingTarget User user);

    @AfterMapping
    protected void fill(User user, @MappingTarget UserResponse response) {
        response.setUserProfile(profiles.toResponse(models.profileForUser(user.getId())));
        response.setRoles(models.roles(user.getId()).stream().map(roles::toResponse).collect(java.util.stream.Collectors.toSet()));
    }
}
