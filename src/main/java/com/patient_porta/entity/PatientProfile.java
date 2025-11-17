package com.patient_porta.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="patient_profiles")
@Getter @Setter @NoArgsConstructor
public class PatientProfile {
    @Id
    @Column(name="user_id")
    private Long userId;

    @OneToOne
    @JoinColumn(name="user_id", insertable=false, updatable=false)
    private User user;

    @Column(name="full_name", nullable=false, length=191)
    private String fullName;

    @Column(name="insurance_number", length=64)
    private String insuranceNumber;

    @Column(name="address", length=255)
    private String address;

    @Column(name="emergency_contact_name", length=128)
    private String emergencyContactName;

    @Column(name="emergency_contact_phone", length=32)
    private String emergencyContactPhone;
}
