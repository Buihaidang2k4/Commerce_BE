package com.example.MyShop_API.service.address;

import com.example.MyShop_API.dto.request.AddressRequest;
import com.example.MyShop_API.dto.request.AddressUpdateRequest;
import com.example.MyShop_API.dto.response.AddressResponse;
import com.example.MyShop_API.entity.Address;
import com.example.MyShop_API.entity.UserProfile;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.constants.CommonMessageUtils;
import com.example.MyShop_API.mapper.AddressMapper;
import com.example.MyShop_API.repo.AddressRepository;
import com.example.MyShop_API.repo.UserProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressService implements IAddressService {
    AddressRepository addressRepository;
    AddressMapper addressMapper;
    UserProfileRepository userProfileRepository;

    @Transactional(readOnly = true)
    public List<AddressResponse> getAllAddresses() {
        return addressRepository.findAll().stream().map(addressMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getAddressByProfileId(Long profileId) {
        return addressRepository.findByProfile_ProfileId(profileId).stream().map(addressMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AddressResponse getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(() ->
                new AppException(CommonMessageUtils.ADDRESS_NOT_EXISTED));

        return addressMapper.toResponse(address);
    }

    @Transactional
    public AddressResponse createAddress(AddressRequest request, Long profileId) {
        log.info("=============START CREATING ADDRESS=========================");
        UserProfile userProfile = userProfileRepository.findById(profileId).orElseThrow(() ->
                new AppException(CommonMessageUtils.USER_NOT_EXISTED));

        Address address = addressMapper.toEntity(request);

        if (request.getIsDefault() != null) {
            if (request.getIsDefault()) {
                addressRepository.clearDefaultAddressForProfile(profileId);
                address.setIsDefault(true);
            } else {
                address.setIsDefault(false);
            }
        }
        // Gan hai chieu
        address.setProfile(userProfile);
        userProfile.getAddress().add(address);

        // luu
        address = addressRepository.save(address);
        log.info("=============END CREATING ADDRESS=========================");
        return addressMapper.toResponse(address);
    }

    @Transactional
    public AddressResponse updateAddress(Long addressId, Long profileId, AddressUpdateRequest request) {
        log.info("===================== START UPDATE ADDRESS ==============");
        Address findAddress = addressRepository.findById(addressId).orElseThrow(() ->
                new AppException(CommonMessageUtils.ADDRESS_NOT_EXISTED));

        addressMapper.updateAddress(request, findAddress);

        if (request.getIsDefault() != null && request.getIsDefault()) {
            List<Address> addresses = addressRepository.findByProfile_ProfileId(profileId);
            addresses.forEach(addr -> addr.setIsDefault(false));
            findAddress.setIsDefault(true);
        }

        findAddress = addressRepository.save(findAddress);

        log.info("===================== END UPDATE ADDRESS ==============");
        return addressMapper.toResponse(findAddress);
    }

    public void deleteAddress(Long addressId, Long profileId) {
        log.info("=====================START DELETE ADDRESS==============");
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(CommonMessageUtils.ADDRESS_NOT_EXISTED));

        if (!address.getProfile().getProfileId().equals(profileId)) {
            throw new AppException(CommonMessageUtils.FORBIDDEN);
        }

        addressRepository.delete(address);
        log.info("=====================END DELETE ADDRESS==============");

    }


}
