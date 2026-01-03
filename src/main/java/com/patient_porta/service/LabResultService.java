package com.patient_porta.service;

import com.patient_porta.dto.LabResultDetailDTO;
import com.patient_porta.dto.LabResultPatientDTO;
import com.patient_porta.entity.Appointment;
import com.patient_porta.entity.PatientProfile;
import com.patient_porta.entity.ProcessLog;
import com.patient_porta.repository.AppointmentRepository;
import com.patient_porta.repository.PatientProfileRepository;
import com.patient_porta.repository.ProcessLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LabResultService {

    private final AppointmentRepository appointmentRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final ProcessLogRepository processLogRepository;

    // =============================
    // DS bệnh nhân có KQ xét nghiệm
    // =============================
    public List<LabResultPatientDTO> getPatientsWithLabResult() {

        return appointmentRepository.findByStatus("COMPLETED")
                .stream()
                .map(a -> {
                    PatientProfile profile = patientProfileRepository
                            .findByUserId(a.getPatientId())
                            .orElse(null);

                    if (profile == null) return null;

                    LabResultPatientDTO dto = new LabResultPatientDTO();
                    dto.setPatientId(a.getPatientId());
                    dto.setFullName(profile.getFullName());
                    dto.setStatus("DONE");

                    return dto;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    // =============================
    // Chi tiết kết quả xét nghiệm
    // =============================
    public LabResultDetailDTO getLabResultDetail(Long patientId) {

        PatientProfile profile = patientProfileRepository
                .findByUserId(patientId)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy hồ sơ bệnh nhân"));

        Appointment appt = appointmentRepository
                .findTopByPatientIdOrderByScheduledAtDesc(patientId);

        if (appt == null) {
            throw new RuntimeException("Không tìm thấy lịch hẹn");
        }

        ProcessLog log = processLogRepository
                .findTopByAppointmentIdOrderByCreatedAtDesc(appt.getId())
                .orElseThrow(() ->
                        new RuntimeException("Chưa có kết quả xét nghiệm"));

        LabResultDetailDTO dto = new LabResultDetailDTO();
        dto.setPatientId(patientId);
        dto.setFullName(profile.getFullName());

        // ✅ TÓM TẮT DỰA TRÊN STATUS
        dto.setSummary("Xét nghiệm đã hoàn tất");

        dto.setCompletedDate(log.getCreatedAt());

        return dto;
    }
}
