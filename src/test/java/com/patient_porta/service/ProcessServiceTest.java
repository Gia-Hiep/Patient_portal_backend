package com.patient_porta.service;

import com.patient_porta.dto.CareFlowStageDTO;
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
class ProcessServiceTest {

    @InjectMocks
    private ProcessService processService;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private CareFlowStageRepository careFlowStageRepository;

    @Test
    void patientSeeProcess_correctStatus() {

        Appointment appt = new Appointment();
        appt.setPatientId(1L);
        appt.setCurrentStageId(2L);
        appt.setScheduledAt(LocalDateTime.now());

        CareFlowStage s1 = new CareFlowStage(1L, 1, "Đang khám");
        CareFlowStage s2 = new CareFlowStage(2L, 2, "Chờ xét nghiệm");
        CareFlowStage s3 = new CareFlowStage(3L, 3, "Hoàn tất");

        when(appointmentRepository.findTopByPatientIdOrderByScheduledAtDesc(1L))
                .thenReturn(appt);

        when(careFlowStageRepository.findAllByOrderByStageOrderAsc())
                .thenReturn(List.of(s1, s2, s3));

        List<CareFlowStageDTO> result =
                processService.getProcessForPatient(1L);

        assertEquals("DONE", result.get(0).getStatus());
        assertEquals("IN_PROGRESS", result.get(1).getStatus());
        assertEquals("NOT_STARTED", result.get(2).getStatus());
    }
}
