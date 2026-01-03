package com.patient_porta.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LabResultDetailDTO {
    private Long patientId;
    private String fullName;
    private String summary;
    private LocalDateTime completedDate;
}
