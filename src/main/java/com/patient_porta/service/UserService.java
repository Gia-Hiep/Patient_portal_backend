package com.patient_porta.service;

import com.patient_porta.dto.UserDTO;
import com.patient_porta.entity.User;
import com.patient_porta.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse login(AuthRequest request) {
        if (request == null || isBlank(request.getUsername()) || isBlank(request.getPassword())) {
            throw new BadCredentialsException("Thiếu username/email hoặc password");
        }

        String id = request.getUsername().trim();

        User user = (id.contains("@")
                ? userRepository.findByEmail(id)
                : userRepository.findByUsername(id).or(() -> userRepository.findByEmail(id))
        ).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getStatus() != User.Status.ACTIVE) {
            throw new org.springframework.security.access.AccessDeniedException("Tài khoản không ACTIVE");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword_hash())) {
            throw new BadCredentialsException("Sai mật khẩu");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, mapToDTO(user));
    }


    private User findByUsernameOrEmail(String id) {
        if (id.contains("@")) {
            return userRepository.findByEmail(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        return userRepository.findByUsername(id)
                .orElseGet(() -> userRepository.findByEmail(id)
                        .orElseThrow(() -> new RuntimeException("User not found")));
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private UserDTO mapToDTO(User u) {
        UserDTO dto = new UserDTO();
        dto.setId(u.getId() == null ? null : u.getId().longValue());
        dto.setUsername(u.getUsername());
        dto.setEmail(u.getEmail());
        dto.setPhone(u.getPhone());
        dto.setRole(u.getRole() != null ? u.getRole().name() : null);
        dto.setStatus(u.getStatus() != null ? u.getStatus().name() : null);
        dto.setCreated_at(u.getCreated_at());
        dto.setUpdated_at(u.getUpdated_at());
        return dto;
    }

    public static class AuthResponse {
        private String token;
        private UserDTO user;

        public AuthResponse(String token, UserDTO user) {
            this.token = token;
            this.user = user;
        }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public UserDTO getUser() { return user; }
        public void setUser(UserDTO user) { this.user = user; }
    }

    public static class AuthRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
