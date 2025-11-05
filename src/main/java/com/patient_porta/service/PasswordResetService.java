package com.patient_porta.service;

import com.patient_porta.entity.PasswordResetToken;
import com.patient_porta.entity.User;
import com.patient_porta.repository.PasswordResetTokenRepository;
import com.patient_porta.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // thời hạn token (phút)
    private static final int EXPIRE_MINUTES = 15;

    public void createAndSendToken(String emailOrUsername, String frontendResetUrlBase) {
        // Cho phép nhập email hoặc username
        User user = (emailOrUsername.contains("@")
                ? userRepository.findByEmail(emailOrUsername)
                : userRepository.findByUsername(emailOrUsername)
                .or(() -> userRepository.findByEmail(emailOrUsername)))
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tạo token
        String token = UUID.randomUUID().toString();
        PasswordResetToken prt = new PasswordResetToken();
        prt.setToken(token);
        prt.setUser(user);
        prt.setExpiryDate(LocalDateTime.now().plusMinutes(EXPIRE_MINUTES));
        tokenRepository.save(prt);

        // Link reset: FE sẽ có trang /reset-password?token=...
        String link = (frontendResetUrlBase != null && !frontendResetUrlBase.isBlank())
                ? frontendResetUrlBase + (frontendResetUrlBase.contains("?") ? "&" : "?") + "token=" + token
                : "http://localhost:3000/reset-password?token=" + token;

        String subject = "Đặt lại mật khẩu Patient Portal";
        String body = "Xin chào " + user.getUsername() + ",\n\n"
                + "Bạn (hoặc ai đó) đã yêu cầu đặt lại mật khẩu cho tài khoản.\n"
                + "Nhấn vào liên kết sau để đặt lại mật khẩu (hết hạn sau " + EXPIRE_MINUTES + " phút):\n"
                + link + "\n\n"
                + "Nếu bạn không yêu cầu thao tác này, hãy bỏ qua email này.";

        emailService.send(user.getEmail(), subject, body);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken prt = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token không hợp lệ"));

        if (prt.isUsed()) {
            throw new RuntimeException("Token đã được sử dụng");
        }
        if (LocalDateTime.now().isAfter(prt.getExpiryDate())) {
            throw new RuntimeException("Token đã hết hạn");
        }

        User user = prt.getUser();
        user.setPassword_hash(passwordEncoder.encode(newPassword));
        user.setUpdated_at(LocalDateTime.now());
        userRepository.save(user);

        prt.setUsed(true);
        tokenRepository.save(prt);
    }

    public boolean validateToken(String token) {
        return tokenRepository.findByToken(token)
                .filter(t -> !t.isUsed() && LocalDateTime.now().isBefore(t.getExpiryDate()))
                .isPresent();
    }
}
