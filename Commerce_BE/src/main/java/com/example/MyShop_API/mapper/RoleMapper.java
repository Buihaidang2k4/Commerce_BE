package com.example.MyShop_API.mapper;
import org.mapstruct.Mapping;

import com.example.MyShop_API.dto.request.RoleRequest;
import com.example.MyShop_API.dto.response.RoleResponse;
import com.example.MyShop_API.entity.Role;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(source = "code", target = "roleName")
    RoleResponse toResponse(Role role);
    @Mapping(source = "roleName", target = "code")
    Role toEntity(RoleRequest roleRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "roleName", target = "code")
    void updateRole(RoleRequest request, @MappingTarget Role role);
}
