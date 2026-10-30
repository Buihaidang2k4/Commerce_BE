package com.example.MyShop_API.mapper;

import com.example.MyShop_API.entity.*;
import com.example.MyShop_API.dto.request.*;
import com.example.MyShop_API.dto.response.*;
import com.example.MyShop_API.service.model.ModelLookup;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserProfileMapper {
    @Autowired protected ModelLookup models;
    @Autowired protected AddressMapper addresses;
    @Autowired protected CartMapper carts;
    public abstract UserProfile toEntity(UserProfileRequest request);
    public abstract UserProfileResponse toResponse(UserProfile profile);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void UpdateUserProfile(UserProfileRequest request, @MappingTarget UserProfile profile);

    @AfterMapping
    protected void fill(UserProfile profile, @MappingTarget UserProfileResponse response) {
        response.setAddressResponse(addresses.toResponse(models.addresses(profile.getProfileId())));
        response.setCartResponse(carts.toResponse(models.cartForProfile(profile.getProfileId())));
    }
}
