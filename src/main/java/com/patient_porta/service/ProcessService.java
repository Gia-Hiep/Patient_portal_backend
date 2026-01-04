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

    // patientId ở đây thực tế là userId của bệnh nhân (PatientProfile.userId)
    public List<CareFlowStageDTO> getProcessForPatient(Long patientId) {

        Appointment latest = appointmentRepository
                .findTopByPatient_UserIdOrderByScheduledAtDesc(patientId)
                .orElse(null);

        if (latest == null) return Collections.emptyList();

        return buildProcess(latest.getCurrentStageId());
    }

    // doctorId ở đây thực tế là userId của bác sĩ (DoctorProfile.userId)
    public List<Map<String, Object>> getAllForDoctor(Long doctorId) {

        List<Appointment> appointments =
                appointmentRepository.findByDoctor_UserIdOrderByScheduledAtAsc(doctorId);

        List<Map<String, Object>> result = new ArrayList<>();

        for (Appointment appt : appointments) {

            Map<String, Object> row = new HashMap<>();
            row.put("appointmentId", appt.getId());
            row.put("patientId", appt.getPatient().getUserId());
            row.put("scheduledAt", appt.getScheduledAt());
            row.put("currentStageId", appt.getCurrentStageId());
            row.put("stages", buildProcess(appt.getCurrentStageId()));

            result.add(row);
        }

        return result;
    }

    private List<CareFlowStageDTO> buildProcess(Long currentStageId) {

        List<CareFlowStage> stages =
                careFlowStageRepository.findAllByOrderByStageOrderAsc();

        Integer currentStageOrder = null;
        Integer maxStageOrder =
                stages.stream()
                        .map(CareFlowStage::getStageOrder)
                        .max(Integer::compareTo)
                        .orElse(null);

        if (currentStageId != null) {
            currentStageOrder = stages.stream()
                    .filter(s -> Objects.equals(s.getId(), currentStageId))
                    .map(CareFlowStage::getStageOrder)
                    .findFirst()
                    .orElse(null);
        }

        List<CareFlowStageDTO> result = new ArrayList<>();

        for (CareFlowStage s : stages) {

            String status;

            if (currentStageOrder == null) {
                status = "NOT_STARTED";
            } else if (s.getStageOrder() < currentStageOrder) {
                status = "DONE";
            } else if (s.getStageOrder().equals(currentStageOrder)) {
                // ✅ STAGE CUỐI → DONE
                if (Objects.equals(currentStageOrder, maxStageOrder)) {
                    status = "DONE";
                } else {
                    status = "IN_PROGRESS";
                }
            } else {
                status = "NOT_STARTED";
            }

            CareFlowStageDTO dto = new CareFlowStageDTO();
            dto.setStageOrder(s.getStageOrder());
            dto.setStageName(s.getStageName());
            dto.setStatus(status);

            result.add(dto);
        }

        return result;
    }
}
