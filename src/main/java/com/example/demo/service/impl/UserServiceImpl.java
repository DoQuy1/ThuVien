package com.example.demo.service.impl;

import com.example.demo.dto.request.FilterRequest;
import com.example.demo.dto.request.LoginDTO;
import com.example.demo.dto.request.RegisterDTO;
import com.example.demo.dto.request.UserDTO;
import com.example.demo.model.Role;
import com.example.demo.model.Token;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.TokenService;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.security.auth.login.AccountLockedException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenService tokenService;

    private AuthenticationManager authenticationManager;



    @Override
    public String login(LoginDTO loginDTO, HttpServletRequest request) {
        User user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new RuntimeException("Sai"));
        if(!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())){
            throw new IllegalArgumentException("Sai mật khẩu");
        }
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword(), user.getAuthorities()));
        String jwtToken = jwtUtil.generateToken(user);
        Token token = tokenService.addToken(user, jwtToken, request);
        return jwtToken;
    }

    @Transactional
    @Override
    public User register(RegisterDTO registerDTO)  {
        if(!registerDTO.getPassword().equals(registerDTO.getRetypePassword())){
            throw new IllegalArgumentException("Mật khẩu không khớp");
        }
        Optional<User> existingUser = userRepository.findByUsername(registerDTO.getUsername());
        if(existingUser.isPresent()){
            throw new IllegalArgumentException("Người dùng đã tồn tại");
        }
        Role role = roleRepository.findByName(Role.USER);
        User user = User
                .builder()
                .firstName(registerDTO.getFirstName())
                .lastName(registerDTO.getLastName())
                .username(registerDTO.getUsername())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .role(role)
                .build();


        return userRepository.save(user);
    }
    @Override
    public Page<User> getAllUsers(FilterRequest filterRequest, Pageable pageable) {
        return userRepository.findByFilter(filterRequest, pageable);
    }

    @Override
    public UserDTO getUserById(Long id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDTO(existingUser);
    }

    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        //Check trùng
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại: " + userDTO.getEmail());
        }
        User user = modelMapper.map(userDTO, User.class);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        return modelMapper.map(userRepository.save(user), UserDTO.class);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        // Lấy người dùng hiện tại từ cơ sở dữ liệu
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Kiểm tra xem email có thay đổi không
        if (!existingUser.getEmail().equals(userDTO.getEmail())) {
            // Chỉ kiểm tra trùng khi email thay đổi
            if (userRepository.existsByEmail(userDTO.getEmail())) {
                throw new IllegalArgumentException("Email đã tồn tại: " + userDTO.getEmail());
            }
        }
        existingUser.setUsername(userDTO.getUsername());
        existingUser.setEmail(userDTO.getEmail());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        if (userDTO.getRoleId() != null) {
            Role role = roleRepository.findById(userDTO.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            existingUser.setRole(role);
        }
        // Lưu và trả về thông tin người dùng đã cập nhật
        return modelMapper.map(userRepository.save(existingUser), UserDTO.class);
    }

    public void saveUsers(List<UserDTO> userDTOs) {
        Set<String> existingEmails = new HashSet<>();

        List<User> users = userDTOs.stream()
                .peek(userDTO -> {
                    if (!existingEmails.add(userDTO.getEmail())) {
                        throw new IllegalArgumentException("Email bị trùng: " + userDTO.getEmail());
                    }
                })
                .map(userDTO -> {
                    User user = modelMapper.map(userDTO, User.class);
                    user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
                    return user;
                })
                .collect(Collectors.toList());

        userRepository.saveAll(users);
    }


    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private UserDTO convertToDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    @Transactional
    public void importUsers(MultipartFile file) throws IOException {
        InputStream excelStream = file.getInputStream();
        Workbook workbook = new XSSFWorkbook(excelStream);
        Sheet sheet = workbook.getSheetAt(0);

        Set<String> fileEmails = new HashSet<>();
        Set<String> dbEmails = new HashSet<>(userRepository.findEmails());

        Map<String, Integer> emailToRowMap = new HashMap<>();
        List<String[]> errors = new ArrayList<>();
        List<UserDTO> userDTOs = new ArrayList<>();

        // Duyệt qua từng hàng trong file Excel
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            String email = row.getCell(2).getStringCellValue();

            // Kiểm tra trùng lặp email trong file Excel
            if (fileEmails.contains(email)) {
                int duplicateRow = emailToRowMap.get(email);
                errors.add(new String[]{String.valueOf(i + 1), "Trùng email với hàng " + (duplicateRow + 1)});
                errors.add(new String[]{String.valueOf(duplicateRow + 1), "Trùng email với hàng " + (i + 1)});
            } else {
                fileEmails.add(email);
                emailToRowMap.put(email, i);
            }

            // Kiểm tra trùng lặp email với database
            if (dbEmails.contains(email)) {
                errors.add(new String[]{String.valueOf(i + 1), "Trùng email với database"});
            }
            // Nếu không có lỗi, thêm vào danh sách DTO
            if (!fileEmails.contains(email) && !dbEmails.contains(email)) {
                UserDTO userDTO = new UserDTO();
                userDTO.setUsername(row.getCell(0).getStringCellValue());
                userDTO.setPassword(row.getCell(1).getStringCellValue());
                userDTO.setEmail(email);
                userDTO.setRoleId((long) row.getCell(3).getNumericCellValue());
                userDTOs.add(userDTO);
            }
        }

        // Nếu có lỗi thì tạo file lỗi và trả về
        if (!errors.isEmpty()) {
            workbook.close();
            throw new IOException("Import failed with errors");
        }

        saveUsers(userDTOs);

        workbook.close();
    }

    private ByteArrayInputStream generateErrorExcel(List<String[]> errors) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Errors");

        int rowIdx = 0;
        for (String[] error : errors) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("Hàng số");
            row.createCell(1).setCellValue("Lỗi");
            row.createCell(0).setCellValue(error[0]);
            row.createCell(1).setCellValue(error[1]);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public void exportUsers() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Users");
            List<User> users = userRepository.findAll();
            int rowIndex = 0;
            for (User user : users) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(user.getUsername());
                row.createCell(1).setCellValue(user.getEmail());
                row.createCell(2).setCellValue(user.getRole().getId());
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to export users", e);
        }
    }


}
