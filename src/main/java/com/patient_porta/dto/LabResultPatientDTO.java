package com.patient_porta.dto;

import lombok.Data;

@Data
public class LabResultPatientDTO {
    private Long patientId;
    private String fullName;
    private String status; // DONE / PENDING
}
