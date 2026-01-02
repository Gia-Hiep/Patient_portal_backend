package com.patient_porta.service;

import com.patient_porta.dto.CareFlowStageDTO;
import com.patient_porta.entity.*;
import com.patient_porta.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ExaminationProgressService {

    private final AppointmentRepository appointmentRepo;
    private final PatientProfileRepository patientProfileRepo;
    private final CareFlowStageRepository stageRepo;
    private final ProcessLogRepository logRepo;

    // =====================================================
    // 👨‍⚕️ BÁC SĨ CẬP NHẬT TRẠNG THÁI
    // =====================================================
    @Transactional
    public void updateStageByPatient(
            Long patientId,
            Long stageId,
            User doctor
    ) {
        if (stageId == null) {
            throw new IllegalArgumentException("Vui lòng chọn trạng thái.");
        }

        if (doctor.getRole() != User.Role.DOCTOR) {
            throw new SecurityException("Bạn không có quyền.");
        }

        Appointment appt = appointmentRepo
                .findTopByPatientIdOrderByScheduledAtDesc(patientId);

        if (appt == null) {
            throw new RuntimeException("Bệnh nhân chưa có lịch khám.");
        }

        CareFlowStage stage = stageRepo.findById(stageId)
                .orElseThrow(() -> new RuntimeException("Trạng thái không hợp lệ"));

        appt.setCurrentStageId(stageId);
        appointmentRepo.save(appt);

        ProcessLog log = new ProcessLog();
        log.setAppointment(appt);
        log.setStage(stage);
        log.setUpdatedBy(doctor);
        logRepo.save(log);
    }

    // =====================================================
    // 👨‍⚕️ BÁC SĨ: DANH SÁCH BỆNH NHÂN + TIẾN TRÌNH
    // =====================================================
    public List<Map<String, Object>> getPatientsForDoctor(Long doctorId) {

        List<Appointment> appointments =
                appointmentRepo.findByDoctorIdOrderByScheduledAtAsc(doctorId);

        List<Map<String, Object>> result = new ArrayList<>();

        for (Appointment appt : appointments) {

            PatientProfile profile =
                    patientProfileRepo.findById(appt.getPatientId()).orElse(null);

            Map<String, Object> row = new HashMap<>();
            row.put("appointmentId", appt.getId());
            row.put("patientId", appt.getPatientId());
            row.put("fullName", profile != null ? profile.getFullName() : "Chưa cập nhật");
            row.put("avatar", "/default-avatar.png");
            row.put("currentStageId", appt.getCurrentStageId());
            row.put("process", buildProcess(appt.getCurrentStageId()));

            result.add(row);
        }

        return result;
    }

    // =====================================================
    // 🧠 CORE LOGIC – TRẠNG THÁI CHUẨN
    // =====================================================
    private List<CareFlowStageDTO> buildProcess(Long currentStageId) {

        List<CareFlowStage> stages =
                stageRepo.findAllByOrderByStageOrderAsc();

        Integer currentStageOrder = null;

        if (currentStageId != null) {
            currentStageOrder = stages.stream()
                    .filter(s -> s.getId().equals(currentStageId))
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
                status = "IN_PROGRESS";
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
