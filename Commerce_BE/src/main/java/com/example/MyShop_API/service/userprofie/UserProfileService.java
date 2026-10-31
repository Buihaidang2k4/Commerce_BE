package com.example.MyShop_API.service.userprofie;

import com.example.MyShop_API.repo.RoleRepository;
import com.example.MyShop_API.repo.ShoppingCartRepository;
import java.util.Optional;

import com.example.MyShop_API.constants.RoleCodeConstants;
import com.example.MyShop_API.dto.request.UserProfileRequest;
import com.example.MyShop_API.dto.response.UserProfileResponse;
import com.example.MyShop_API.entity.UserAddress;
import com.example.MyShop_API.entity.ShoppingCart;
import com.example.MyShop_API.entity.UserAccount;
import com.example.MyShop_API.entity.UserProfile;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.constants.CommonMessageConstants;
import com.example.MyShop_API.mapper.UserProfileMapper;
import com.example.MyShop_API.repo.UserProfileRepository;
import com.example.MyShop_API.repo.UserAccountRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileService implements IUserProfileService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final RoleRepository roleRepository;
    UserProfileRepository userProfileRepository;
    UserProfileMapper userProfileMapper;
    UserAccountRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserProfileResponse> getUserProfile() {
        return userProfileRepository.findAll().stream().map(userProfileMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfileById(Long userProfileId) throws AppException {
        return userProfileMapper.toResponse(userProfileRepository.findById(userProfileId).orElseThrow(
                () -> new AppException(CommonMessageConstants.USER_NOT_EXISTED)
        ));
    }


    @Transactional
    public UserProfileResponse createOrUpdateProfile(Long userId, UserProfileRequest userProfileRequest) {
        UserAccount user = userRepository.findByIdForUpdate(userId).orElseThrow(() -> new AppException(CommonMessageConstants.USER_NOT_EXISTED));
        if (roleRepository.findAllByUserId(userId).stream().noneMatch(role -> RoleCodeConstants.USER.equals(role.getCode()))) throw new AppException(CommonMessageConstants.ROLE_NOT_ALLOWED);
        UserProfile profile = Optional.ofNullable(userId).flatMap(userProfileRepository::findByUserId).orElse(null);
        if (profile == null) {
            profile = createEmptyUserProfile();
            profile.setUserId(userId);
            ShoppingCart cart = Optional.ofNullable(profile.getId()).flatMap(shoppingCartRepository::findByUserProfileId).orElse(null);
            cart.setUserId(userId);
            shoppingCartRepository.save(cart);
        }
        userProfileMapper.UpdateUserProfile(userProfileRequest, profile);
        return userProfileMapper.toResponse(userProfileRepository.save(profile));
    }

    @Override
    @Transactional
    public UserProfile createEmptyUserProfile() {
        UserProfile profile = userProfileRepository.save(new UserProfile());
        shoppingCartRepository.save(ShoppingCart.builder().userProfileId(profile.getId()).build());
        return profile;
    }
}
