package com.example.MyShop_API.controller;

import com.example.MyShop_API.constants.ApiPathConstants;

import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.dto.request.UserProfileRequest;
import com.example.MyShop_API.dto.response.UserProfileResponse;
import com.example.MyShop_API.service.userprofie.IUserProfileService;
import com.example.MyShop_API.service.userprofie.UserProfileService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(ApiPathConstants.BASE_USER_PROFILE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileController {
    IUserProfileService userProfileService;

    @GetMapping(ApiPathConstants.USER_PROFILE_ALL)
    public ResponseEntity<ApiResponse> getAllProfiles() {
        return ResponseEntity.ok(new ApiResponse(200, "all profile", userProfileService.getUserProfile()));
    }

    @GetMapping(ApiPathConstants.USER_PROFILE_PROFILE_ACCOUNT_ID)
    ResponseEntity<ApiResponse> getUserProfile(@PathVariable Long account_id) {
        return ResponseEntity.ok(new ApiResponse(200, "profile by id", userProfileService.getUserProfileById(account_id)));
    }

    @PostMapping(ApiPathConstants.USER_PROFILE_USER_USER_ID_CREATE_OR_UPDATE_PROFILE)
    ResponseEntity<ApiResponse> addUserProfile(@Valid @RequestBody UserProfileRequest userProfileRequest, @PathVariable Long userId) {
        UserProfileResponse response = userProfileService.createOrUpdateProfile(userId, userProfileRequest);
        return ResponseEntity.ok(new ApiResponse(200, "profile created", response));
    }
}
