package com.patient_porta.service;

import com.patient_porta.dto.UserDTO;
import com.patient_porta.entity.PatientProfile;
import com.patient_porta.entity.User;
import com.patient_porta.repository.PatientProfileRepository;
import com.patient_porta.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private PatientProfileRepository profiles;

    @InjectMocks
    private UserService userService;

    private User activePatient;

    @BeforeEach
    void setUp() {
        activePatient = new User();
        activePatient.setId(100L);
        activePatient.setUsername("patient01");
        activePatient.setEmail("patient01@example.com");
        activePatient.setPhone("0900000002");
        activePatient.setPassword_hash("ENCODED");
        activePatient.setRole(User.Role.PATIENT);
        activePatient.setStatus(User.Status.ACTIVE);
    }

    //  TEST LOGIN

    @Test
    @DisplayName("Login thành công bằng email")
    void loginSuccessByEmail() {
        UserService.AuthRequest req = new UserService.AuthRequest();
        req.setUsername("patient01@example.com");
        req.setPassword("12345678");

        when(userRepository.findByEmail("patient01@example.com"))
                .thenReturn(Optional.of(activePatient));
        when(passwordEncoder.matches("12345678", "ENCODED"))
                .thenReturn(true);
        when(jwtService.generateToken(activePatient))
                .thenReturn("fake-jwt-token");

        UserService.AuthResponse res = userService.login(req);

        assertNotNull(res);
        assertEquals("fake-jwt-token", res.getToken());
        assertEquals("patient01", res.getUser().getUsername());
        verify(userRepository).findByEmail("patient01@example.com");
    }

    @Test
    @DisplayName("Login sai mật khẩu -> ném BadCredentialsException")
    void loginWrongPassword() {
        UserService.AuthRequest req = new UserService.AuthRequest();
        req.setUsername("patient01@example.com");
        req.setPassword("WRONG");

        when(userRepository.findByEmail("patient01@example.com"))
                .thenReturn(Optional.of(activePatient));
        when(passwordEncoder.matches("WRONG", "ENCODED"))
                .thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> userService.login(req));
    }

    @Test
    @DisplayName("Login user không tồn tại -> UsernameNotFoundException")
    void loginUserNotFound() {
        UserService.AuthRequest req = new UserService.AuthRequest();
        req.setUsername("notfound@example.com");
        req.setPassword("12345678");

        when(userRepository.findByEmail("notfound@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.login(req));
    }

}

