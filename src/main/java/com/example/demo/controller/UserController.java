package com.example.demo.controller;

import com.example.demo.dto.request.FilterRequest;
import com.example.demo.dto.request.LoginDTO;
import com.example.demo.dto.request.RegisterDTO;
import com.example.demo.dto.request.UserDTO;
import com.example.demo.dto.response.Response;
import com.example.demo.dto.response.UserPage;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import static com.example.demo.common.ResponseCode.ERROR_CODE;
import static com.example.demo.common.ResponseCode.SUCCESS_CODE;
import static com.example.demo.common.ReponseMessage.*;

@Api(value = "User Management System")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Response<?>> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request){
        try{
            String token = userService.login(loginDTO, request);
            return ResponseEntity.ok().body(
                    new Response<>(SUCCESS_CODE, "Đăng nhập thành công ", token)
            );
        }catch (Exception e){
            return ResponseEntity.badRequest().body(
                    new Response<>(ERROR_CODE, e.getMessage())
            );
        }
    }
    @PostMapping("/register")
    public ResponseEntity<Response<User>> register(@RequestBody RegisterDTO registerDTO){
        try{
            User user = userService.register(registerDTO);
            return ResponseEntity.ok().body(
                    new Response<>(SUCCESS_CODE, "Đăng kí thành công", user)
            );
        }catch (Exception e){
            return ResponseEntity.badRequest().body(
                    new Response<>(ERROR_CODE, e.getMessage())
            );
        }
    }
    @PostMapping("/filter")
    public ResponseEntity<Response<UserPage>> filterUsers(@RequestBody FilterRequest filterRequest, Pageable pageable) {
        try {
            // Lấy danh sách người dùng theo filterRequest
            Page<User> users = userService.getAllUsers(filterRequest, pageable);

            // Tạo UserPage từ Page<User>
            UserPage userPage = UserPage.builder()
                    .users(users.getContent())
                    .page(pageable.getPageNumber())
                    .size(pageable.getPageSize())
                    .totalPages(users.getTotalPages())
                    .totalElements(users.getTotalElements())
                    .build();

            return ResponseEntity.ok().body(new Response<>(SUCCESS_CODE, "Filtered users", userPage));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(ERROR_CODE, e.getMessage()));
        }
    }


    @ApiOperation(value = "Get a user by Id", response = UserDTO.class)
    @GetMapping("/{id}")
    public ResponseEntity<Response<UserDTO>> getUserById(@PathVariable Long id) {
        try {
            UserDTO user = userService.getUserById(id);
            return ResponseEntity.ok().body(
                    new Response<>(SUCCESS_CODE, "User found", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new Response<>(ERROR_CODE, e.getMessage(), null));
        }
    }


    @ApiOperation(value = "Create a new user", response = UserDTO.class)
    @PostMapping
    public ResponseEntity<Response<UserDTO>> createUser(@RequestBody UserDTO userDTO) {
        try {
            UserDTO user = userService.createUser(userDTO);
            return ResponseEntity.ok().body(
                    new Response<>(SUCCESS_CODE, CREATE_USER_SUCCESSFULLY, user)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new Response<>(ERROR_CODE, CREATE_USER_FAILED, null)
            );
        }
    }

    @ApiOperation(value = "Update an existing user", response = UserDTO.class)
    @PutMapping("/{id}")
    public ResponseEntity<Response<UserDTO>> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        try {
            UserDTO updatedUser = userService.updateUser(id, userDTO);
            return ResponseEntity.ok().body(
                    new Response<>(SUCCESS_CODE, UPDATE_USER_SUCCESSFULLY, updatedUser)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new Response<>(ERROR_CODE, UPDATE_USER_FAILED, null)
            );
        }
    }

    @ApiOperation(value = "Delete a user by Id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().body(
                    new Response<>(SUCCESS_CODE, DELETE_USER_SUCCESSFULLY, null)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new Response<>(ERROR_CODE, DELETE_USER_FAILED, null)
            );
        }
    }

    @ApiOperation(value = "Import users from an Excel file")
    @PostMapping("/import")
    public ResponseEntity<Response<String>> importUsers(@RequestParam("file") MultipartFile file) {
        try {
            userService.importUsers(file);
            return ResponseEntity.ok().body(
                    new Response<>(SUCCESS_CODE, IMPORT_USER_SUCCESSFULLY, null)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new Response<>(ERROR_CODE, IMPORT_USER_FAILED, e.getMessage())
            );
        }
    }

    @ApiOperation(value = "Export users to an Excel file")
    @GetMapping("/export")
    public ResponseEntity<Response<String>> exportUsers() {
        try {
            userService.exportUsers();
            return ResponseEntity.ok().body(
                    new Response<>(SUCCESS_CODE, EXPORT_USER_SUCCESSFULLY, null)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new Response<>(ERROR_CODE, EXPORT_USER_FAILED, e.getMessage())
            );
        }
    }
}
