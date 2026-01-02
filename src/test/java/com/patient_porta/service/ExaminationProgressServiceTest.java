package com.patient_porta.service;

import com.patient_porta.entity.*;
import com.patient_porta.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
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

        Appointment appt = new Appointment();
        appt.setId(10L);
        appt.setPatientId(100L);
        appt.setScheduledAt(LocalDateTime.now());

        CareFlowStage stage = new CareFlowStage();
        stage.setId(2L);
        stage.setStageOrder(2);

        when(appointmentRepo.findTopByPatientIdOrderByScheduledAtDesc(100L))
                .thenReturn(appt);
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
}
