package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.request.AddressRequest;
import com.example.MyShop_API.dto.request.AddressUpdateRequest;
import com.example.MyShop_API.dto.response.AddressResponse;
import com.example.MyShop_API.entity.Address;
import org.mapstruct.*;
import org.springframework.util.StringUtils;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address toEntity(AddressRequest addressRequest);

    @Mapping(target = "shortAddress", expression = "java(buildShortAddress(address))")
    @Mapping(target = "fullAddress", expression = "java(buildFullAddress(address))")
    @Mapping(target = "createdAt", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    @Mapping(target = "updatedAt", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    AddressResponse toResponse(Address address);

    List<AddressResponse> toResponse(List<Address> addresses);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAddress(AddressUpdateRequest addressRequest, @MappingTarget Address address);

    default String buildShortAddress(Address a) {
        return String.format("%s, %s, %s", a.getStreet(), a.getDistrict(), a.getProvince());
    }

    default String buildFullAddress(Address a) {
        return String.format("%s, %s, %s, %s, %s, %s, %s",
                a.getFullName(),
                a.getPhone(),
                a.getStreet(),
                a.getWard(),
                a.getDistrict(),
                a.getProvince(),
                StringUtils.hasText(a.getAdditionalInfo()) ? "(" + a.getAdditionalInfo() + ")" : ""
        ).replaceAll(", \\(", " (");
    }
}
