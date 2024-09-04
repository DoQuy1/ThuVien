package com.example.demo.repository;

import com.example.demo.model.RelaRolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Role;
import com.example.demo.model.Permission;
import java.util.List;
import java.util.Optional;

@Repository
public interface RolePermissionRepository extends JpaRepository<RelaRolePermission, Long> {
    List<RelaRolePermission> findAllByRoleId(Long roleId);
    Optional<RelaRolePermission> findByRoleAndPermission(Role role, Permission permission);
}
