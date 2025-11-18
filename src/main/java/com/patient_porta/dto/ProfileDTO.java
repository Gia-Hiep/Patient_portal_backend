package com.patient_porta.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileDTO {
    private String fullName;
    private LocalDate dateOfBirth;
    private String address;
    private String phone;
    private String email;
    private String insuranceNumber;
    private String emergencyContactName;
    private String emergencyContactPhone;
}
