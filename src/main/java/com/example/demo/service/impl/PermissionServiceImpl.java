
package com.example.demo.service.impl;

import com.example.demo.dto.request.PermissionDTO;
import com.example.demo.model.Permission;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.service.PermissionService;
import com.example.demo.util.ConvertUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public Permission createPermission(PermissionDTO permissionDTO) {
        Permission newPermission = ConvertUtil.convertObject(permissionDTO, object -> modelMapper.map(object, Permission.class));
        return permissionRepository.save(newPermission);
    }

    @Transactional
    @Override
    public Permission updatePermission(Long permissionId, PermissionDTO permissionDTO) {
        Permission existPermission = permissionRepository
                .findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền"));
        ConvertUtil.convertObject(permissionDTO, object -> {
            modelMapper.map(permissionDTO, existPermission);
            return existPermission;
        });
        return permissionRepository.save(existPermission);
    }

    @Transactional
    @Override
    public void deletePermission(Long permissionId) {
        Permission existPermission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy id"));
    }


}
