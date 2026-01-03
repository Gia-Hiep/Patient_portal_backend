package com.patient_porta.dto.admin;

import lombok.Data;

@Data
public class AdminUserDTO {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String role;
    private String status; // ACTIVE / LOCKED
}
