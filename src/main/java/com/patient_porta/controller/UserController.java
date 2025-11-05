package com.patient_porta.controller;

import com.patient_porta.entity.User;
import com.patient_porta.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.patient_porta.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    @PostMapping("/register-test")
    public ResponseEntity<?> registerTest() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword_hash(passwordEncoder.encode("123456")); // ← quan trọng
        user.setRole(User.Role.PATIENT);
        user.setStatus(User.Status.ACTIVE);
        userRepository.save(user);
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/login")
    public ResponseEntity<UserService.AuthResponse> login(@RequestBody UserService.AuthRequest request) {
        UserService.AuthResponse authResponse = userService.login(request);
        return ResponseEntity.ok(authResponse);
    }

}
