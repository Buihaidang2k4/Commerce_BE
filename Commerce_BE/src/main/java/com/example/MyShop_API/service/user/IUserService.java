package com.example.MyShop_API.service.user;

import com.example.MyShop_API.dto.request.ChangePasswordRequest;
import com.example.MyShop_API.dto.request.UserCreationRequest;
import com.example.MyShop_API.dto.request.UserUpdateRequest;
import com.example.MyShop_API.dto.response.UserResponse;
import com.example.MyShop_API.entity.User;

import java.security.Principal;
import java.util.List;

public interface IUserService {
    List<UserResponse> getUsers();

    UserResponse getUserById(Long id);

    User findUserByPrincipal(Principal principal);

    UserResponse createUser(UserCreationRequest request);

    void changePassword(ChangePasswordRequest request, Long id);

    UserResponse updateRoleUser(UserUpdateRequest request, Long id);

    void lockUser(Long userId, String reason, Principal principal);

    void unlockUser(Long userId);

    void deleteUserById(Long id);

    UserResponse getMyInfor();
}
