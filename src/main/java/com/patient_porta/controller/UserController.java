package com.patient_porta.controller;

import com.patient_porta.dto.UserDTO;
import com.patient_porta.entity.User;
import com.patient_porta.repository.UserRepository;
import com.patient_porta.service.PasswordResetService;
import com.patient_porta.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final PasswordResetService passwordResetService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserService.RegisterRequest req) {
        UserDTO dto = userService.register(req);
        return ResponseEntity.status(201).body(dto);
    }

    @PostMapping("/register-test")
    public ResponseEntity<?> registerTest() {
        String username = "admin";
        String email = "admin@example.com";

        if (userRepository.existsByUsername(username) || userRepository.existsByEmail(email)) {
            return ResponseEntity.ok("EXISTS"); // đã tồn tại => không tạo nữa để tránh lỗi unique
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword_hash(passwordEncoder.encode("123456"));
        user.setRole(User.Role.ADMIN);
        user.setStatus(User.Status.ACTIVE);
        user.setCreated_at(java.time.LocalDateTime.now());
        user.setUpdated_at(java.time.LocalDateTime.now());
        userRepository.save(user);
        return ResponseEntity.ok("OK");
    }
    @PostMapping("/register-test-2")
    public ResponseEntity<?> registerTest2() {
        String username = "doctor";
        String email = "doctor@example.com";

        if (userRepository.existsByUsername(username) || userRepository.existsByEmail(email)) {
            return ResponseEntity.ok("EXISTS"); // đã tồn tại => không tạo nữa để tránh lỗi unique
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword_hash(passwordEncoder.encode("123456"));
        user.setRole(User.Role.DOCTOR);
        user.setStatus(User.Status.ACTIVE);
        user.setCreated_at(java.time.LocalDateTime.now());
        user.setUpdated_at(java.time.LocalDateTime.now());
        userRepository.save(user);
        return ResponseEntity.ok("OK");
    }
    @PostMapping("/login")
    public ResponseEntity<UserService.AuthResponse> login(@RequestBody UserService.AuthRequest request) {
        UserService.AuthResponse authResponse = userService.login(request);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody ForgotRequest req,
            @RequestParam(value = "redirect", required = false) String redirectUrl
    ) {
        passwordResetService.createAndSendToken(req.getIdentifier(), redirectUrl);
        return ResponseEntity.ok(new SimpleMessage("Nếu tài khoản tồn tại, liên kết đặt lại mật khẩu đã được gửi."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetRequest req) {
        passwordResetService.resetPassword(req.getToken(), req.getNewPassword());
        return ResponseEntity.ok(new SimpleMessage("Đặt lại mật khẩu thành công"));
    }

    // ===== DTOs =====
    @Data
    public static class ForgotRequest {
        private String identifier; // email hoặc username
    }

    @Data
    public static class ResetRequest {
        private String token;
        private String newPassword;
    }

    @Data
    public static class SimpleMessage {
        private final String message;
    }
}
