package com.patient_porta.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VisitSummaryDTO {
    private Long id;
    private LocalDateTime visitDate;   // appointments.scheduled_at
    private String department;         // khoa khám (doctor_profiles.department hoặc services.name)
    private String doctorName;         // bác sĩ phụ trách
    private String diagnosisShort;     // chẩn đoán tóm tắt (dùng appointments.notes)
    private String status;             // REQUESTED / CONFIRMED / COMPLETED ...
}
