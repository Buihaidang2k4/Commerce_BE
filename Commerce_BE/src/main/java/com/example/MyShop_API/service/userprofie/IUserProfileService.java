package com.example.MyShop_API.service.userprofie;

import com.example.MyShop_API.dto.request.UserProfileRequest;
import com.example.MyShop_API.dto.response.UserProfileResponse;
import com.example.MyShop_API.entity.UserProfile;

import java.util.List;

public interface IUserProfileService {
    List<UserProfileResponse> getUserProfile();

    UserProfileResponse getUserProfileById(Long userProfileId);

    UserProfileResponse createOrUpdateProfile(Long userId, UserProfileRequest userProfileRequest);

    UserProfile createEmptyUserProfile();
}
