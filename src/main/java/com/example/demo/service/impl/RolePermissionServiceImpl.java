
package com.example.demo.service.impl;

import com.example.demo.dto.request.RolePermissionDTO;
import com.example.demo.model.Permission;
import com.example.demo.model.Role;
import com.example.demo.model.RelaRolePermission;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.repository.RolePermissionRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.service.RolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RolePermissionServiceImpl implements RolePermissionService {
    private final RolePermissionRepository rolePermissionRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public List<RelaRolePermission> findAllByRoleId(Long roleId) {
        return rolePermissionRepository.findAllByRoleId(roleId);
    }
    @Transactional
    @Override
    public RelaRolePermission addRolePermissionToRole(RolePermissionDTO rolePermissionDTO) {

        Role existRole = roleRepository
                .findById(rolePermissionDTO.getRoleId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy role"));
        Permission existPermission = permissionRepository
                .findById(rolePermissionDTO.getPermissionId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền"));
        Optional<RelaRolePermission> optionalRolePermission = rolePermissionRepository.findByRoleAndPermission(existRole, existPermission);
        if (optionalRolePermission.isPresent()) {
            RelaRolePermission existRolePermission = optionalRolePermission.get();
            return rolePermissionRepository.save(existRolePermission);
        } else {
            RelaRolePermission newRolePermission = new RelaRolePermission();
            newRolePermission.setRole(existRole);
            newRolePermission.setPermission(existPermission);
            return rolePermissionRepository.save(newRolePermission);
        }
    }
}
