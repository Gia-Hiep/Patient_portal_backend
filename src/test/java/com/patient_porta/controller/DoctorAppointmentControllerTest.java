package com.patient_porta.controller;

import com.patient_porta.dto.DoctorAppointmentResponse;
import com.patient_porta.entity.User;
import com.patient_porta.repository.UserRepository;
import com.patient_porta.service.DoctorAppointmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorAppointmentControllerTest {

    @Mock DoctorAppointmentService service;
    @Mock UserRepository userRepository;

    @InjectMocks DoctorAppointmentController controller;

    @Test
    void getAppointments_happyPath_readsPrincipal_findsDoctorId_callsService() {
        Principal principal = () -> "doctor01";

        User doctorUser = new User();
        doctorUser.setId(3L);

        when(userRepository.findByUsername("doctor01")).thenReturn(Optional.of(doctorUser));

        List<DoctorAppointmentResponse> fake = List.of(
                new DoctorAppointmentResponse("A", LocalDateTime.of(2025, 12, 21, 8, 30), "REQUESTED")
        );

        when(service.getAppointmentsForDoctor(3L, "WAITING", "2025-12-21")).thenReturn(fake);

        List<DoctorAppointmentResponse> res =
                controller.getAppointments("WAITING", "2025-12-21", principal);

        assertThat(res).hasSize(1);
        assertThat(res.get(0).getPatientName()).isEqualTo("A");

        verify(userRepository).findByUsername("doctor01");
        verify(service).getAppointmentsForDoctor(3L, "WAITING", "2025-12-21");
    }

    @Test
    void getAppointments_principalNull_throws() {
        assertThatThrownBy(() -> controller.getAppointments(null, null, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unauthenticated");
        verifyNoInteractions(userRepository, service);
    }

    @Test
    void getAppointments_doctorNotFound_throws() {
        Principal principal = () -> "doctorXX";
        when(userRepository.findByUsername("doctorXX")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.getAppointments(null, null, principal))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Doctor not found");

        verify(userRepository).findByUsername("doctorXX");
        verifyNoInteractions(service);
    }
}
