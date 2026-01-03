package com.patient_porta.service;

import com.patient_porta.dto.CareFlowStageDTO;
import com.patient_porta.entity.Appointment;
import com.patient_porta.entity.CareFlowStage;
import com.patient_porta.entity.PatientProfile;
import com.patient_porta.repository.AppointmentRepository;
import com.patient_porta.repository.CareFlowStageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessServiceTest {

    @InjectMocks
    private ProcessService processService;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private CareFlowStageRepository careFlowStageRepository;

    @Test
    void patientSeeProcess_correctStatus() {
        // ===== setup appointment (patient là PatientProfile có userId) =====
        PatientProfile pp = new PatientProfile();
        pp.setUserId(1L);

        Appointment appt = new Appointment();
        appt.setPatient(pp);
        appt.setCurrentStageId(2L);
        appt.setScheduledAt(LocalDateTime.now());

        // ===== stages =====
        CareFlowStage s1 = new CareFlowStage(1L, 1, "Đang khám");
        CareFlowStage s2 = new CareFlowStage(2L, 2, "Chờ xét nghiệm");
        CareFlowStage s3 = new CareFlowStage(3L, 3, "Hoàn tất");

        // ✅ repo method mới + Optional
        when(appointmentRepository.findTopByPatient_UserIdOrderByScheduledAtDesc(1L))
                .thenReturn(Optional.of(appt));

        when(careFlowStageRepository.findAllByOrderByStageOrderAsc())
                .thenReturn(List.of(s1, s2, s3));

        // ===== run =====
        List<CareFlowStageDTO> result = processService.getProcessForPatient(1L);

        // ===== assert =====
        assertEquals("DONE", result.get(0).getStatus());
        assertEquals("IN_PROGRESS", result.get(1).getStatus());
        assertEquals("NOT_STARTED", result.get(2).getStatus());
    }
}
