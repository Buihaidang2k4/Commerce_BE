package com.example.MyShop_API.mapper;

import com.example.MyShop_API.repo.ShoppingCartRepository;
import com.example.MyShop_API.repo.UserAddressRepository;
import java.util.Optional;

import com.example.MyShop_API.entity.*;
import com.example.MyShop_API.dto.request.*;
import com.example.MyShop_API.dto.response.*;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserProfileMapper {
    @Autowired protected ShoppingCartRepository shoppingCartRepository;
    @Autowired protected UserAddressRepository userAddressRepository;
    @Autowired protected UserAddressMapper addresses;
    @Autowired protected ShoppingCartMapper carts;

    @Mapping(source = "username", target = "displayName")
    @Mapping(source = "mobileNumber", target = "phoneNumber")
    public abstract UserProfile toEntity(UserProfileRequest request);


    @Mapping(source = "id", target = "profileId")
    @Mapping(source = "displayName", target = "username")
    @Mapping(source = "phoneNumber", target = "mobileNumber")
    @BeanMapping(builder = @Builder(disableBuilder = true))
    public abstract UserProfileResponse toResponse(UserProfile profile);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)


    @Mapping(target = "id", ignore = true)
    @Mapping(source = "username", target = "displayName")
    @Mapping(source = "mobileNumber", target = "phoneNumber")
    public abstract void UpdateUserProfile(UserProfileRequest request, @MappingTarget UserProfile profile);

    @AfterMapping
    protected void fill(UserProfile profile, @MappingTarget UserProfileResponse response) {
        response.setAddressResponse(addresses.toResponse(Optional.ofNullable(profile.getId()).map(userAddressRepository::findByUserProfileId).orElseGet(List::of)));
        response.setCartResponse(carts.toResponse(Optional.ofNullable(profile.getId()).flatMap(shoppingCartRepository::findByUserProfileId).orElse(null)));
    }
}
