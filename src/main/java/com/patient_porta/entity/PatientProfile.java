package com.patient_porta.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "patient_profiles")
@Data
public class PatientProfile {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "sex")
    @Enumerated(EnumType.STRING)
    private Sex sex;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "address")
    private String address;

    @Column(name = "insurance_number")
    private String insuranceNumber; // Mã BHYT

    @Column(name = "emergency_contact_name")
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;

    public enum Sex {
        M, F, O
    }

}
