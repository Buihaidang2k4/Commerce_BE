package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.request.UserProfileRequest;
import com.example.MyShop_API.dto.response.UserProfileResponse;
import com.example.MyShop_API.entity.UserProfile;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {AddressMapper.class, CartMapper.class})
public interface UserProfileMapper {
    UserProfile toEntity(UserProfileRequest userProfileRequest);

    @Mapping(source = "address", target = "addressResponse")
    @Mapping(source = "cart", target = "cartResponse")
    UserProfileResponse toResponse(UserProfile userProfile);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void UpdateUserProfile(UserProfileRequest userProfileRequest, @MappingTarget UserProfile userProfile);
}
