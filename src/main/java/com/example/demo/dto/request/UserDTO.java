package com.example.demo.dto.request;

import lombok.Data;

@Data
public class UserDTO {
    private String username;
    private String password;
    private String email;
    private Long roleId;
}


