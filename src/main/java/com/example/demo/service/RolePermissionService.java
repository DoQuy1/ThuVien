package com.example.demo.service;

import com.example.demo.dto.request.RolePermissionDTO;
import com.example.demo.model.Role;
import com.example.demo.model.RelaRolePermission;

import java.util.List;

public interface RolePermissionService {
    List<RelaRolePermission> findAllByRoleId(Long roleId);
    RelaRolePermission addRolePermissionToRole(RolePermissionDTO rolePermissionDTO);

}