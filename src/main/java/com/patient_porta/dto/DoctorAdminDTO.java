package com.patient_porta.dto;

import lombok.Data;

/**
 * DTO dùng cho Admin quản lý danh sách bác sĩ (US14.2)
 */
@Data
public class DoctorAdminDTO {
    private Long id;            // userId
    private String username;
    private String email;
    private String status;      // ACTIVE/LOCKED/DISABLED

    private String fullName;
    private String specialty;
    private String department;
    private String licenseNo;
    private String bio;
    private String workingSchedule;
}
