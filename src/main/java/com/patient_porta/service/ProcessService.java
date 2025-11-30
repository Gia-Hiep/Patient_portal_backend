package com.patient_porta.service;

import com.patient_porta.dto.CareFlowStageDTO;
import com.patient_porta.entity.Appointment;
import com.patient_porta.entity.CareFlowStage;
import com.patient_porta.repository.AppointmentRepository;
import com.patient_porta.repository.CareFlowStageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProcessService {

    private final AppointmentRepository appointmentRepository;
    private final CareFlowStageRepository careFlowStageRepository;

    /**
     * patientId ở đây chính là users.id của bệnh nhân
     */
    public List<CareFlowStageDTO> getProcessForPatient(Long patientId) {

        System.out.println("[PROCESS] → Tìm appointment mới nhất cho patientUserId=" + patientId);

        // dùng method mới, trả Optional
        Optional<Appointment> optLatest =
                appointmentRepository.findTopByPatient_User_IdOrderByScheduledAtDesc(patientId);

        if (optLatest.isEmpty()) {
            System.out.println("[PROCESS] → Không có appointment nào. Trả về list rỗng.");
            return Collections.emptyList();
        }

        Appointment latest = optLatest.get();

        System.out.println("[PROCESS] → appointmentId = " + latest.getId()
                + " | scheduled_at = " + latest.getScheduledAt()
                + " | status = " + latest.getStatus());

        List<CareFlowStage> stages =
                careFlowStageRepository.findByAppointmentIdOrderByStageOrder(latest.getId());

        System.out.println("[PROCESS] → Tổng số stages = " + stages.size());

        List<CareFlowStageDTO> result = new ArrayList<>();

        for (CareFlowStage s : stages) {
            System.out.println("[PROCESS] → Stage " + s.getStageOrder() + ": "
                    + s.getStageName() + " | " + s.getStatus());

            CareFlowStageDTO dto = new CareFlowStageDTO();
            dto.setStageOrder(s.getStageOrder());
            dto.setStageName(s.getStageName());
            dto.setStatus(s.getStatus());

            result.add(dto);
        }

        return result;
    }
}
