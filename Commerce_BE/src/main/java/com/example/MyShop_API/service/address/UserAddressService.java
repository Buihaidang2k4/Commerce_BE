package com.example.MyShop_API.service.address;

import com.example.MyShop_API.dto.request.AddressRequest;
import com.example.MyShop_API.dto.request.AddressUpdateRequest;
import com.example.MyShop_API.dto.response.AddressResponse;
import com.example.MyShop_API.entity.UserAddress;
import com.example.MyShop_API.entity.UserProfile;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.constants.CommonMessageConstants;
import com.example.MyShop_API.mapper.UserAddressMapper;
import com.example.MyShop_API.repo.UserAddressRepository;
import com.example.MyShop_API.repo.UserProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserAddressService implements IUserAddressService {
    UserAddressRepository addressRepository;
    UserAddressMapper addressMapper;
    UserProfileRepository userProfileRepository;

    @Transactional(readOnly = true)
    public List<AddressResponse> getAllAddresses() {
        return addressRepository.findAll().stream().map(addressMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getAddressByProfileId(Long profileId) {
        return addressRepository.findByUserProfileId(profileId).stream().map(addressMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AddressResponse getAddressById(Long addressId) {
        UserAddress address = addressRepository.findById(addressId).orElseThrow(() ->
                new AppException(CommonMessageConstants.ADDRESS_NOT_EXISTED));

        return addressMapper.toResponse(address);
    }

    @Transactional
    public AddressResponse createAddress(AddressRequest request, Long profileId) {
        log.info("=============START CREATING ADDRESS=========================");
        UserProfile userProfile = userProfileRepository.findById(profileId).orElseThrow(() ->
                new AppException(CommonMessageConstants.USER_NOT_EXISTED));

        UserAddress address = addressMapper.toEntity(request);

        if (request.getIsDefault() != null) {
            if (request.getIsDefault()) {
                addressRepository.clearDefaultAddressForProfile(profileId);
                address.setDefaultShippingAddress(true);
            } else {
                address.setDefaultShippingAddress(false);
            }
        }
        // Gan hai chieu
        address.setUserProfileId(userProfile.getId());


        // luu
        address = addressRepository.save(address);
        log.info("=============END CREATING ADDRESS=========================");
        return addressMapper.toResponse(address);
    }

    @Transactional
    public AddressResponse updateAddress(Long addressId, Long profileId, AddressUpdateRequest request) {
        log.info("===================== START UPDATE ADDRESS ==============");
        UserAddress findAddress = addressRepository.findById(addressId).orElseThrow(() ->
                new AppException(CommonMessageConstants.ADDRESS_NOT_EXISTED));

        if (!java.util.Objects.equals(findAddress.getUserProfileId(), profileId)) throw new AppException(CommonMessageConstants.FORBIDDEN);
        addressMapper.updateAddress(request, findAddress);

        if (request.getIsDefault() != null && request.getIsDefault()) {
            List<UserAddress> addresses = addressRepository.findByUserProfileId(profileId);
            addresses.forEach(addr -> addr.setDefaultShippingAddress(false));
            findAddress.setDefaultShippingAddress(true);
        }

        findAddress = addressRepository.save(findAddress);

        log.info("===================== END UPDATE ADDRESS ==============");
        return addressMapper.toResponse(findAddress);
    }

    public void deleteAddress(Long addressId, Long profileId) {
        log.info("=====================START DELETE ADDRESS==============");
        UserAddress address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(CommonMessageConstants.ADDRESS_NOT_EXISTED));

        if (!java.util.Objects.equals(address.getUserProfileId(), profileId)) {
            throw new AppException(CommonMessageConstants.FORBIDDEN);
        }

        addressRepository.delete(address);
        log.info("=====================END DELETE ADDRESS==============");

    }


}
