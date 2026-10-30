package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.request.UserCreationRequest;
import com.example.MyShop_API.dto.request.UserUpdateRequest;
import com.example.MyShop_API.dto.response.UserResponse;
import com.example.MyShop_API.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {UserProfileMapper.class})
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User toEntity(UserCreationRequest request);

    @Mapping(source = "profile", target = "userProfile")
    UserResponse toResponse(User user);

    User toEntity(UserResponse userResponse);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(UserUpdateRequest request, @MappingTarget User user);
}
