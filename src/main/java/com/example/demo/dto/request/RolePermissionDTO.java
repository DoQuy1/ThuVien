package com.example.demo.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RolePermissionDTO {
    @JsonProperty("role_id")
    private Long roleId;
    @JsonProperty("permission_id")
    private Long permissionId;
}