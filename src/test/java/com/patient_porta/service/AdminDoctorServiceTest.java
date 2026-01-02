package com.patient_porta.service;

import com.patient_porta.dto.CreateDoctorRequest;
import com.patient_porta.dto.DoctorAdminDTO;
import com.patient_porta.dto.UpdateDoctorRequest;
import com.patient_porta.entity.DoctorProfile;
import com.patient_porta.entity.User;
import com.patient_porta.repository.DoctorProfileRepository;
import com.patient_porta.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminDoctorServiceTest {

    @Mock UserRepository userRepo;
    @Mock DoctorProfileRepository doctorProfileRepo;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks AdminDoctorService service;

    @Test
    void listDoctors_includeDisabledFalse_shouldUseRoleAndStatusNotDisabled() {
        User u1 = new User(); u1.setId(3L); u1.setUsername("doctor01"); u1.setEmail("doctor01@example.com"); u1.setRole(User.Role.DOCTOR); u1.setStatus(User.Status.ACTIVE);
        when(userRepo.findAllByRoleAndStatusNot(User.Role.DOCTOR, User.Status.DISABLED)).thenReturn(List.of(u1));

        DoctorProfile p1 = new DoctorProfile(); p1.setFullName("BS. Lê Văn C"); p1.setSpecialty("Nội"); p1.setDepartment("Khám"); p1.setLicenseNo("L1");
        when(doctorProfileRepo.findById(3L)).thenReturn(Optional.of(p1));

        List<DoctorAdminDTO> out = service.listDoctors(false);

        assertEquals(1, out.size());
        assertEquals(3L, out.get(0).getId());
        assertEquals("doctor01", out.get(0).getUsername());
        assertEquals("BS. Lê Văn C", out.get(0).getFullName());

        verify(userRepo).findAllByRoleAndStatusNot(User.Role.DOCTOR, User.Status.DISABLED);
        verify(userRepo, never()).findAllByRole(User.Role.DOCTOR);
    }

    @Test
    void listDoctors_includeDisabledTrue_shouldUseRoleOnly() {
        User u1 = new User(); u1.setId(8L); u1.setUsername("doctor02"); u1.setEmail("doctor02@hospital.local"); u1.setRole(User.Role.DOCTOR); u1.setStatus(User.Status.DISABLED);
        when(userRepo.findAllByRole(User.Role.DOCTOR)).thenReturn(List.of(u1));
        when(doctorProfileRepo.findById(8L)).thenReturn(Optional.empty());

        List<DoctorAdminDTO> out = service.listDoctors(true);

        assertEquals(1, out.size());
        assertEquals("doctor02", out.get(0).getUsername());
        assertEquals("DISABLED", out.get(0).getStatus());

        verify(userRepo).findAllByRole(User.Role.DOCTOR);
        verify(userRepo, never()).findAllByRoleAndStatusNot(any(), any());
    }

    @Test
    void createDoctor_success_defaultPassword_123456() {
        CreateDoctorRequest req = new CreateDoctorRequest();
        req.setUsername("lego");
        req.setEmail("concac@gmail.com");
        req.setPassword(""); // default 123456
        req.setFullName("Bác sĩ Lego");
        req.setSpecialty("Lego");
        req.setDepartment("Đồ chơi");
        req.setLicenseNo("C1");
        req.setWorkingSchedule("T2,T5,T7");

        when(userRepo.existsByUsername("lego")).thenReturn(false);
        when(userRepo.existsByEmail("concac@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("ENC_123456");

        when(userRepo.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(29L);
            return u;
        });

        when(doctorProfileRepo.save(any(DoctorProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        DoctorAdminDTO dto = service.createDoctor(req);

        assertEquals(29L, dto.getId());
        assertEquals("lego", dto.getUsername());
        assertEquals("concac@gmail.com", dto.getEmail());
        assertEquals("ACTIVE", dto.getStatus());
        assertEquals("Bác sĩ Lego", dto.getFullName());
        assertEquals("Lego", dto.getSpecialty());

        verify(passwordEncoder).encode("123456");
        verify(userRepo).save(any(User.class));
        verify(doctorProfileRepo).save(any(DoctorProfile.class));
    }

    @Test
    void createDoctor_blankUsername_shouldThrow400() {
        CreateDoctorRequest req = new CreateDoctorRequest();
        req.setUsername("   ");
        req.setEmail("a@b.com");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.createDoctor(req));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void updateDoctor_notFound_shouldThrow404() {
        when(userRepo.findById(999L)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.updateDoctor(999L, new UpdateDoctorRequest()));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void disableDoctor_userNotDoctor_shouldThrow400() {
        User u = new User(); u.setId(5L); u.setRole(User.Role.PATIENT);
        when(userRepo.findById(5L)).thenReturn(Optional.of(u));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.disableDoctor(5L));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }
}
