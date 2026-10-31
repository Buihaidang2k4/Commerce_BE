package com.example.MyShop_API.service.role;

import com.example.MyShop_API.dto.request.RoleRequest;
import com.example.MyShop_API.dto.response.RoleResponse;
import com.example.MyShop_API.entity.Role;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.constants.CommonMessageUtils;
import com.example.MyShop_API.mapper.RoleMapper;
import com.example.MyShop_API.repo.RoleRepository;
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
public class RoleService implements IRoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;

    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        log.debug("getAllRoles()");
        return roleRepository.findAll().stream().map(roleMapper::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RoleResponse getRoleById(String id) {
        log.debug("getRoleById({})", id);
        Role role = roleRepository.findByCode(id).orElseThrow(() ->
                new AppException(CommonMessageUtils.USER_NOT_EXISTED));

        return roleMapper.toResponse(role);
    }

    public RoleResponse createRole(RoleRequest roleRequest) {
        log.debug("createRole({})", roleRequest);
        Role role = roleMapper.toEntity(roleRequest);
        if (role.getName() == null) role.setName(role.getCode());
        return roleMapper.toResponse(roleRepository.save(role));
    }

    public RoleResponse updateRole(String id, RoleRequest roleRequest) {
        log.debug("updateRole({})", id);
        Role role = roleRepository.findByCode(id).orElseThrow(() ->
                new AppException(CommonMessageUtils.USER_NOT_EXISTED));

        roleMapper.updateRole(roleRequest, role);
        return roleMapper.toResponse(roleRepository.saveAndFlush(role));
    }

    @Transactional
    public void deleteRole(String id) {
        log.debug("deleteRole({})", id);
        roleRepository.deleteByCode(id);
    }

}
