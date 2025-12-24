package com.patient_porta.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class VisitDetailDTO {
    private Long id;
    private LocalDateTime visitDate;

    private String department;
    private String doctorName;
    private String serviceName;

    private String diagnosisShort;
    private String diagnosisDetail;
    private String prescription;       // đơn thuốc
    private String treatmentHistory;   // lịch sử điều trị
    private String status;

    private List<VisitDocumentDTO> documents;  // danh sách PDF
}
