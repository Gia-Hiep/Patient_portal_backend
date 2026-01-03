package com.patient_porta.service;

import com.patient_porta.entity.*;
import com.patient_porta.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExaminationProgressServiceTest {

    @InjectMocks
    private ExaminationProgressService service;

    @Mock
    private AppointmentRepository appointmentRepo;

    @Mock
    private PatientProfileRepository patientProfileRepo;

    @Mock
    private CareFlowStageRepository stageRepo;

    @Mock
    private ProcessLogRepository logRepo;

    // ============================
    // ✅ DOCTOR UPDATE STAGE
    // ============================
    @Test
    void updateStageByPatient_success() {
        User doctor = new User();
        doctor.setId(1L);
        doctor.setRole(User.Role.DOCTOR);

        // patient profile (PK = userId)
        PatientProfile pp = new PatientProfile();
        pp.setUserId(100L);

        Appointment appt = new Appointment();
        appt.setId(10L);
        appt.setPatient(pp);
        appt.setScheduledAt(LocalDateTime.now());

        CareFlowStage stage = new CareFlowStage();
        stage.setId(2L);
        stage.setStageOrder(2);

        // ✅ repo method mới + Optional
        when(appointmentRepo.findTopByPatient_UserIdOrderByScheduledAtDesc(100L))
                .thenReturn(Optional.of(appt));
        when(stageRepo.findById(2L)).thenReturn(Optional.of(stage));

        service.updateStageByPatient(100L, 2L, doctor);

        assertEquals(2L, appt.getCurrentStageId());
        verify(appointmentRepo).save(appt);
        verify(logRepo).save(any(ProcessLog.class));
    }

    // ============================
    // ❌ KHÔNG PHẢI DOCTOR
    // ============================
    @Test
    void updateStageByPatient_notDoctor_throwException() {
        User user = new User();
        user.setRole(User.Role.PATIENT);

        assertThrows(SecurityException.class, () ->
                service.updateStageByPatient(1L, 1L, user));
    }

    // (optional) thêm case: patient chưa có lịch khám
    @Test
    void updateStageByPatient_noAppointment_throwRuntime() {
        User doctor = new User();
        doctor.setRole(User.Role.DOCTOR);

        when(appointmentRepo.findTopByPatient_UserIdOrderByScheduledAtDesc(100L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                service.updateStageByPatient(100L, 2L, doctor));
    }
}
