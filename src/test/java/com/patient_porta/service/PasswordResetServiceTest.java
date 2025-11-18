package com.patient_porta.service;

import com.patient_porta.entity.PasswordResetToken;
import com.patient_porta.entity.User;
import com.patient_porta.repository.PasswordResetTokenRepository;
import com.patient_porta.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordResetTokenRepository tokenRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    EmailService emailService;

    @InjectMocks
    PasswordResetService passwordResetService;

    @Test
    @DisplayName("createAndSendToken: tìm user theo email, tạo token và gửi email")
    void createAndSendToken_byEmail() {
        User u = new User();
        u.setId(10L);
        u.setUsername("patient01");
        u.setEmail("patient01@example.com");

        when(userRepository.findByEmail("patient01@example.com"))
                .thenReturn(Optional.of(u));
        when(tokenRepository.save(any(PasswordResetToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        passwordResetService.createAndSendToken(
                "patient01@example.com",
                "http://localhost:3000/reset-password"
        );

        // verify có lưu token và gửi mail
        verify(tokenRepository, times(1)).save(any(PasswordResetToken.class));
        verify(emailService, times(1))
                .send(eq("patient01@example.com"), contains("Đặt lại mật khẩu"), anyString());
    }

    @Test
    @DisplayName("createAndSendToken: tìm user theo username cũng được")
    void createAndSendToken_byUsername() {
        User u = new User();
        u.setId(10L);
        u.setUsername("patient01");
        u.setEmail("patient01@example.com");

        when(userRepository.findByUsername("patient01"))
                .thenReturn(Optional.of(u));

        when(tokenRepository.save(any(PasswordResetToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        passwordResetService.createAndSendToken("patient01", null);

        verify(tokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).send(eq("patient01@example.com"), anyString(), anyString());
    }

    @Test
    @DisplayName("resetPassword: token hợp lệ -> đổi mật khẩu và đánh dấu used")
    void resetPassword_success() {
        User u = new User();
        u.setId(10L);
        u.setUsername("patient01");
        u.setEmail("patient01@example.com");

        PasswordResetToken prt = new PasswordResetToken();
        prt.setId(1L);
        prt.setToken("abc");
        prt.setUser(u);
        prt.setUsed(false);
        prt.setExpiryDate(LocalDateTime.now().plusMinutes(10));

        when(tokenRepository.findByToken("abc"))
                .thenReturn(Optional.of(prt));
        when(passwordEncoder.encode("newpass"))
                .thenReturn("encoded");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(tokenRepository.save(any(PasswordResetToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        passwordResetService.resetPassword("abc", "newpass");

        assertEquals("encoded", u.getPassword_hash());
        assertTrue(prt.isUsed());
        verify(userRepository).save(u);
        verify(tokenRepository).save(prt);
    }

    @Test
    @DisplayName("resetPassword: token không tồn tại -> throw RuntimeException")
    void resetPassword_invalidToken() {
        when(tokenRepository.findByToken("not-exist"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> passwordResetService.resetPassword("not-exist", "12345678")
        );

        assertTrue(ex.getMessage().contains("không hợp lệ"));
    }

    @Test
    @DisplayName("validateToken: token hợp lệ -> true")
    void validateToken_valid() {
        PasswordResetToken prt = new PasswordResetToken();
        prt.setToken("abc");
        prt.setUsed(false);
        prt.setExpiryDate(LocalDateTime.now().plusMinutes(5));

        when(tokenRepository.findByToken("abc"))
                .thenReturn(Optional.of(prt));

        assertTrue(passwordResetService.validateToken("abc"));
    }

    @Test
    @DisplayName("validateToken: token đã dùng hoặc hết hạn -> false")
    void validateToken_invalidOrExpired() {
        // đã dùng
        PasswordResetToken used = new PasswordResetToken();
        used.setToken("used");
        used.setUsed(true);
        used.setExpiryDate(LocalDateTime.now().plusMinutes(5));

        when(tokenRepository.findByToken("used"))
                .thenReturn(Optional.of(used));
        assertFalse(passwordResetService.validateToken("used"));

        // hết hạn
        PasswordResetToken expired = new PasswordResetToken();
        expired.setToken("expired");
        expired.setUsed(false);
        expired.setExpiryDate(LocalDateTime.now().minusMinutes(1));

        when(tokenRepository.findByToken("expired"))
                .thenReturn(Optional.of(expired));
        assertFalse(passwordResetService.validateToken("expired"));
    }
}
