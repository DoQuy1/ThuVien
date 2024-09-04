package com.example.demo.service;

import com.example.demo.dto.request.LoginDTO;
import com.example.demo.dto.request.RegisterDTO;
import com.example.demo.dto.request.UserDTO;
import com.example.demo.model.User;
import com.example.demo.dto.request.FilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

public interface UserService {

    String login(LoginDTO loginDTO, HttpServletRequest request) throws Exception;

    User register(RegisterDTO registerDTO) throws Exception;
    Page<User> getAllUsers(FilterRequest filterRequest, Pageable pageable);
    UserDTO getUserById(Long id);
    UserDTO createUser(UserDTO userDTO);
    @Transactional
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    void importUsers(MultipartFile file)  throws IOException;
    void exportUsers();
    void saveUsers(List<UserDTO> userDTOs);

}
