package com.patient_porta.dto.admin;

import lombok.Data;

@Data
public class AdminUserCreateRequest {
    private String username;
    private String email;
    private String phone;
    private String password;
    private String role; // PATIENT / DOCTOR / ADMIN
}
