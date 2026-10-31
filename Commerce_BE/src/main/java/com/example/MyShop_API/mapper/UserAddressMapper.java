package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.request.AddressRequest;
import com.example.MyShop_API.dto.request.AddressUpdateRequest;
import com.example.MyShop_API.dto.response.AddressResponse;
import com.example.MyShop_API.entity.UserAddress;
import org.mapstruct.*;
import org.springframework.util.StringUtils;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserAddressMapper {






    @Mapping(source = "fullName", target = "recipientName")
    @Mapping(source = "phone", target = "recipientPhone")
    @Mapping(source = "districtID", target = "districtCode")
    @Mapping(source = "provinceID", target = "provinceCode")
    @Mapping(source = "additionalInfo", target = "deliveryInstructions")
    @Mapping(source = "isDefault", target = "defaultShippingAddress")
    @Mapping(source = "type", target = "addressType")
    UserAddress toEntity(AddressRequest addressRequest);

    @Mapping(target = "shortAddress", expression = "java(buildShortAddress(address))")
    @Mapping(target = "fullAddress", expression = "java(buildFullAddress(address))")








    @Mapping(source = "id", target = "addressId")
    @Mapping(source = "recipientName", target = "fullName")
    @Mapping(source = "recipientPhone", target = "phone")
    @Mapping(source = "districtCode", target = "districtID")
    @Mapping(source = "provinceCode", target = "provinceID")
    @Mapping(source = "deliveryInstructions", target = "additionalInfo")
    @Mapping(source = "defaultShippingAddress", target = "isDefault")
    @Mapping(source = "addressType", target = "type")
    AddressResponse toResponse(UserAddress address);

    List<AddressResponse> toResponse(List<UserAddress> addresses);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)





    @Mapping(target = "id", ignore = true)
    @Mapping(source = "fullName", target = "recipientName")
    @Mapping(source = "phone", target = "recipientPhone")
    @Mapping(source = "additionalInfo", target = "deliveryInstructions")
    @Mapping(source = "isDefault", target = "defaultShippingAddress")
    @Mapping(source = "type", target = "addressType")
    void updateAddress(AddressUpdateRequest addressRequest, @MappingTarget UserAddress address);

    default String buildShortAddress(UserAddress a) {
        return String.format("%s, %s, %s", a.getStreet(), a.getDistrict(), a.getProvince());
    }

    default String buildFullAddress(UserAddress a) {
        return String.format("%s, %s, %s, %s, %s, %s, %s",
                a.getRecipientName(),
                a.getRecipientPhone(),
                a.getStreet(),
                a.getWard(),
                a.getDistrict(),
                a.getProvince(),
                StringUtils.hasText(a.getDeliveryInstructions()) ? "(" + a.getDeliveryInstructions() + ")" : ""
        ).replaceAll(", \\(", " (");
    }
}
