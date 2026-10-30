package com.example.MyShop_API.service.address;

import com.example.MyShop_API.dto.request.AddressRequest;
import com.example.MyShop_API.dto.request.AddressUpdateRequest;
import com.example.MyShop_API.dto.response.AddressResponse;
import com.example.MyShop_API.entity.Address;

import java.util.List;

public interface IAddressService {
    List<AddressResponse> getAllAddresses();

    AddressResponse getAddressById(Long addressId);

    List<AddressResponse> getAddressByProfileId(Long profileId);

    AddressResponse createAddress(AddressRequest addressRequest, Long userProfileId);

    AddressResponse updateAddress(Long addressId, Long profileId, AddressUpdateRequest addressRequest);

    void deleteAddress(Long addressId, Long profileId);
}
