package com.patient_porta.dto;

import lombok.Data;

/**
 * Request body cho Admin cập nhật thông tin bác sĩ (US14.2)
 */
@Data
public class UpdateDoctorRequest {
    private String fullName;
    private String specialty;
    private String department;
    private String licenseNo;
    private String bio;
    private String workingSchedule;
}
