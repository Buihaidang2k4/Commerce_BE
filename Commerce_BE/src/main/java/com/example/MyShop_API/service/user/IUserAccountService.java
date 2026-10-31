package com.example.MyShop_API.service.user;

import com.example.MyShop_API.dto.request.ChangePasswordRequest;
import com.example.MyShop_API.dto.request.UserCreationRequest;
import com.example.MyShop_API.dto.request.UserUpdateRequest;
import com.example.MyShop_API.dto.response.UserResponse;
import com.example.MyShop_API.entity.UserAccount;

import java.security.Principal;
import java.util.List;

public interface IUserAccountService {
    List<UserResponse> getUsers();

    UserResponse getUserById(Long id);

    UserAccount findUserByPrincipal(Principal principal);

    UserResponse createUser(UserCreationRequest request);

    void changePassword(ChangePasswordRequest request, Long id);

    UserResponse updateRoleUser(UserUpdateRequest request, Long id);

    void lockUser(Long userId, String reason, Principal principal);

    void unlockUser(Long userId);

    void deleteUserById(Long id);

    UserResponse getMyInfor();
}
