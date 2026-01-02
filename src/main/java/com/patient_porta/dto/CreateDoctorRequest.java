package com.patient_porta.dto;

import lombok.Data;

/**
 * US14.2 - Admin tạo mới bác sĩ
 */
@Data
public class CreateDoctorRequest {
    private String username;
    private String email;
    private String password;        // có thể cho mặc định
    private String fullName;
    private String specialty;
    private String department;
    private String licenseNo;
    private String workingSchedule;
}
