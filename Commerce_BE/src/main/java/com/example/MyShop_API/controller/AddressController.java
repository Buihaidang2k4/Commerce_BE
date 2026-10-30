package com.example.MyShop_API.controller;

import com.example.MyShop_API.constants.ApiPath;

import com.example.MyShop_API.dto.request.AddressRequest;
import com.example.MyShop_API.dto.request.AddressUpdateRequest;
import com.example.MyShop_API.dto.response.AddressResponse;
import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.entity.Address;
import com.example.MyShop_API.mapper.AddressMapper;
import com.example.MyShop_API.service.address.AddressService;
import com.example.MyShop_API.service.address.IAddressService;
import com.example.MyShop_API.service.authentication.IAuthenticationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(ApiPath.BASE_ADDRESS)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressController {
    IAddressService addressService;


    @GetMapping(ApiPath.ADDRESS_ALL)
    ApiResponse<List<AddressResponse>> getAllAddresses() {
        return ApiResponse.<List<AddressResponse>>builder()
                .code(200)
                .message("Success")
                .data(addressService.getAllAddresses())
                .build();
    }

    @GetMapping(ApiPath.ADDRESS_ADDRESS_ADDRESS_ID)
    ApiResponse<AddressResponse> getAddress(@PathVariable Long addressId) {
        return ApiResponse.<AddressResponse>builder()
                .code(200)
                .message("Success")
                .data(addressService.getAddressById(addressId))
                .build();
    }

    @GetMapping(ApiPath.ADDRESS_PROFILE_PROFILE_ID)
    ResponseEntity<ApiResponse<List<AddressResponse>>> getAddressByProfile(@PathVariable Long profileId) {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Success", addressService.getAddressByProfileId(profileId)));
    }

    @PostMapping(ApiPath.ADDRESS_USER_PROFILE_USER_PROFILE_ID_ADD)
    ApiResponse<AddressResponse> createAddress(@Valid @RequestBody AddressRequest addressRequest, @PathVariable Long userProfileId) {
        return ApiResponse.<AddressResponse>builder()
                .data(addressService.createAddress(addressRequest, userProfileId))
                .build();
    }

    @PutMapping(ApiPath.ADDRESS_UPDATE)
    ApiResponse<AddressResponse> updateAddress(@Valid @RequestBody AddressUpdateRequest addressRequest
            , @RequestParam Long addressId
            , @RequestParam Long profileId
    ) {
        return ApiResponse.<AddressResponse>builder()
                .data(addressService.updateAddress(addressId, profileId, addressRequest))
                .build();
    }

    @DeleteMapping(ApiPath.ADDRESS_DELETE)
    ApiResponse<Void> deleteAddress(
            @RequestParam Long addressId,
            @RequestParam Long profileId

    ) {
        addressService.deleteAddress(addressId, profileId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Success")
                .build();
    }

}
