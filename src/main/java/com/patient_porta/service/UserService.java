package com.patient_porta.service;

import com.patient_porta.dto.UserDTO;
import com.patient_porta.entity.PatientProfile;
import com.patient_porta.entity.User;
import com.patient_porta.repository.PatientProfileRepository;
import com.patient_porta.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PatientProfileRepository profiles;
    // ================== ĐĂNG NHẬP (đã có) ==================
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

    // ================== ĐĂNG KÝ (THÊM MỚI) ==================
    public UserDTO register(RegisterRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("Dữ liệu đăng ký không hợp lệ");
        }
        if (isBlank(req.getFullName())) {
            throw new IllegalArgumentException("Vui lòng nhập họ tên");
        }
        if (isBlank(req.getEmailOrPhone())) {
            throw new IllegalArgumentException("Vui lòng nhập Email hoặc SĐT");
        }
        if (isBlank(req.getPassword()) || req.getPassword().length() < 8) {
            throw new IllegalArgumentException("Mật khẩu tối thiểu 8 ký tự");
        }
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new IllegalArgumentException("Xác nhận mật khẩu không khớp");
        }
        if (Boolean.FALSE.equals(req.getAgreeTerms())) {
            throw new IllegalArgumentException("Bạn phải đồng ý với điều khoản");
        }

        String email = null;
        String phone = null;
        String rawId = req.getEmailOrPhone().trim();

        if (rawId.contains("@")) {
            // Đăng ký bằng email
            email = rawId.toLowerCase();
            if (userRepository.findByEmail(email).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email đã tồn tại trong hệ thống !");
            }
        } else {
            // Đăng ký bằng SĐT: lưu phone + tạo email kỹ thuật để đảm bảo unique (nếu schema bắt buộc)
            phone = rawId.replaceAll("\\s+", "");
            email = phone + "@phone.local";
            if (userRepository.findByEmail(email).isPresent()) {
                throw new IllegalStateException("Số điện thoại đã được dùng để đăng ký");
            }
        }

        // Sinh username: lấy phần trước @ hoặc p + timestamp
        String username = email.contains("@")
                ? email.substring(0, email.indexOf('@'))
                : "p" + System.currentTimeMillis();

        // Tạo user mới
        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPhone(phone);
        u.setPassword_hash(passwordEncoder.encode(req.getPassword()));
        u.setRole(User.Role.PATIENT);       // hoặc role mặc định khác nếu bạn muốn
        u.setStatus(User.Status.ACTIVE);

        u = userRepository.save(u);

        PatientProfile p = new PatientProfile();
        p.setUserId(u.getId());
        p.setFullName(req.getFullName());
        profiles.save(p);
        return mapToDTO(u);
    }

    // ================== HÀM PHỤ DÙNG CHUNG ==================

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

    // ================== DTO dùng cho login/register ==================

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

    // Request đăng ký (match phía frontend)
    public static class RegisterRequest {
        private String fullName;
        private String emailOrPhone;
        private String password;
        private String confirmPassword;
        private Boolean agreeTerms;

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getEmailOrPhone() { return emailOrPhone; }
        public void setEmailOrPhone(String emailOrPhone) { this.emailOrPhone = emailOrPhone; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getConfirmPassword() { return confirmPassword; }
        public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

        public Boolean getAgreeTerms() { return agreeTerms; }
        public void setAgreeTerms(Boolean agreeTerms) { this.agreeTerms = agreeTerms; }


    }
}