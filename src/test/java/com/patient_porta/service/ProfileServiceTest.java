package com.patient_porta.service;

import com.patient_porta.dto.ProfileDTO;
import com.patient_porta.entity.PatientProfile;
import com.patient_porta.entity.User;
import com.patient_porta.repository.PatientProfileRepository;
import com.patient_porta.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PatientProfileRepository patientProfileRepository;

    @Mock
    SecurityContext securityContext;

    @Mock
    Authentication authentication;

    @InjectMocks
    ProfileService profileService;

    private User patientUser;

    @BeforeEach
    void setup() {
        patientUser = new User();
        patientUser.setId(2L);
        patientUser.setUsername("patient01");
        patientUser.setEmail("patient01@example.com");
        patientUser.setPhone("0900000002");
        patientUser.setRole(User.Role.PATIENT);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("patient01");
        when(userRepository.findByUsername("patient01")).thenReturn(Optional.of(patientUser));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("getMyProfile: đã có profile trong DB -> trả về DTO đúng")
    void getMyProfile_existingProfile() {
        PatientProfile p = new PatientProfile();
        p.setUserId(2L);
        p.setUser(patientUser);
        p.setFullName("Nguyễn Văn A");
        p.setDateOfBirth(LocalDate.of(1995, 5, 10));
        p.setAddress("Đà Nẵng");
        p.setInsuranceNumber("BHYT123");
        p.setEmergencyContactName("Anh B");
        p.setEmergencyContactPhone("0909xxxxxx");

        when(patientProfileRepository.findById(2L)).thenReturn(Optional.of(p));

        ProfileDTO dto = profileService.getMyProfile();

        assertEquals("Nguyễn Văn A", dto.getFullName());
        assertEquals(LocalDate.of(1995, 5, 10), dto.getDateOfBirth());
        assertEquals("Đà Nẵng", dto.getAddress());
        assertEquals("BHYT123", dto.getInsuranceNumber());
        assertEquals("Anh B", dto.getEmergencyContactName());
        assertEquals("0909xxxxxx", dto.getEmergencyContactPhone());
        assertEquals("0900000002", dto.getPhone());
        assertEquals("patient01@example.com", dto.getEmail());
    }

    @Test
    @DisplayName("getMyProfile: chưa có profile -> tự tạo default profile")
    void getMyProfile_createDefaultIfNotExists() {
        when(patientProfileRepository.findById(2L)).thenReturn(Optional.empty());

        ArgumentCaptor<PatientProfile> captor = ArgumentCaptor.forClass(PatientProfile.class);
        when(patientProfileRepository.save(any(PatientProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProfileDTO dto = profileService.getMyProfile();

        verify(patientProfileRepository).save(captor.capture());
        PatientProfile saved = captor.getValue();

        // default fullName lấy từ username
        assertEquals("patient01", saved.getFullName());
        assertEquals("patient01", dto.getFullName());
    }

    @Test
    @DisplayName("getMyProfile: user không phải PATIENT -> ném exception")
    void getMyProfile_notPatient() {
        patientUser.setRole(User.Role.ADMIN); // đổi role

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> profileService.getMyProfile());

        assertTrue(ex.getMessage().contains("Chỉ bệnh nhân"));
    }

    @Test
    @DisplayName("updateMyProfile: cập nhật thông tin profile + user")
    void updateMyProfile_updateOk() {
        PatientProfile p = new PatientProfile();
        p.setUserId(2L);
        p.setUser(patientUser);
        p.setFullName("Old Name");

        when(patientProfileRepository.findById(2L)).thenReturn(Optional.of(p));
        when(patientProfileRepository.save(any(PatientProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProfileDTO dto = new ProfileDTO();
        dto.setFullName("Tên mới");
        dto.setDateOfBirth(LocalDate.of(2000, 1, 1));
        dto.setAddress("HCM");
        dto.setInsuranceNumber("BHYT999");
        dto.setEmergencyContactName("Ba Mẹ");
        dto.setEmergencyContactPhone("0912xxxxxx");
        dto.setPhone("0988000111");
        dto.setEmail("new@example.com");

        ProfileDTO result = profileService.updateMyProfile(dto);

        // verify đã cập nhật & lưu
        verify(patientProfileRepository).save(p);
        verify(userRepository).save(patientUser);

        assertEquals("Tên mới", result.getFullName());
        assertEquals("HCM", result.getAddress());
        assertEquals("0988000111", result.getPhone());
        assertEquals("new@example.com", result.getEmail());
    }
}
