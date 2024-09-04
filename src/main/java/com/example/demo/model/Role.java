package com.example.demo.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;

    @Column(unique = true)
    private String name;


    @OneToMany(mappedBy = "role")
    private List<RelaRolePermission> rolePermissionList;

    public static final String ADMIN = "Admin";
    public static final String USER = "User";
}
