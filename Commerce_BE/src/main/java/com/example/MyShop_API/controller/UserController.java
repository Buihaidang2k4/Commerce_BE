package com.example.MyShop_API.controller;

import com.example.MyShop_API.constants.ApiPath;

import com.example.MyShop_API.dto.request.ChangePasswordRequest;
import com.example.MyShop_API.dto.request.UserCreationRequest;
import com.example.MyShop_API.dto.request.UserUpdateRequest;
import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.dto.response.UserResponse;
import com.example.MyShop_API.entity.User;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.constants.CommonMessageUtils;
import com.example.MyShop_API.service.user.IUserService;
import com.example.MyShop_API.service.user.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(ApiPath.BASE_USER)
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class UserController {
    IUserService userService;

    @GetMapping(ApiPath.USER_ALL)
    ResponseEntity<ApiResponse> getUsers() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username: {}", authentication.getName());
        List<UserResponse> ds = userService.getUsers();
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "list users", ds));
    }

    @GetMapping(ApiPath.USER_ID)
    ApiResponse<UserResponse> getUserById(@PathVariable Long id) {
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .data(userService.getUserById(id))
                .build();
    }

    @PostMapping(ApiPath.USER_REGISTRATION)
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .data(userService.createUser(request))
                .build();
    }

    @PutMapping(ApiPath.USER_ID_UPDATE_ROLE_USER)
    ApiResponse<UserResponse> updateUser(
            @Valid @RequestBody UserUpdateRequest request
            , @PathVariable Long id) {
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .data(userService.updateRoleUser(request, id))
                .build();
    }

    @PutMapping(ApiPath.USER_USER_ID_LOCK_USER)
    ResponseEntity<ApiResponse> lockeUser(
            @PathVariable Long userId,
            @RequestParam("lockReason") String lockReason,
            Principal principal
    ) {

        userService.lockUser(userId, lockReason, principal);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "locked user success", userId));
    }

    @PutMapping(ApiPath.USER_USER_ID_UNLOCKED_USER)
    ResponseEntity<ApiResponse> unlockedUser(
            @PathVariable Long userId) {
        userService.unlockUser(userId);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "unlocked user success", userId));
    }


    @PutMapping(ApiPath.USER_USER_ID_CHANGE_PASSWORD)
    ResponseEntity<ApiResponse<?>> changePassword(
            @PathVariable(name = "userId") Long userId
            , @RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(request, userId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Change password successful", HttpStatus.OK));
    }

    @DeleteMapping(ApiPath.USER_ID_DELETE)
    void deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
    }

    @GetMapping(ApiPath.USER_MY_INFO)
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .data(userService.getMyInfor())
                .build();
    }

}
