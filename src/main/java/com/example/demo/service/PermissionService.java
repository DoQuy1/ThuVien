
package com.example.demo.service;

import com.example.demo.dto.request.PermissionDTO;
import com.example.demo.model.Permission;

public interface PermissionService {
    Permission createPermission(PermissionDTO permissionDTO);
    Permission updatePermission(Long permissionId, PermissionDTO permissionDTO) ;
    void deletePermission(Long permissionId) ;
}
