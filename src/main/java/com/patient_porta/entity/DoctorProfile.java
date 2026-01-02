package com.patient_porta.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "doctor_profiles")
@Data
public class DoctorProfile {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "specialty")
    private String specialty;

    @Column(name = "department")
    private String department;

    @Column(name = "license_no")
    private String licenseNo;

    @Column(name = "bio")
    private String bio;

    /**
     * Lịch làm việc / khung giờ làm việc (US14.2)
     * Ví dụ: "T2-T6 08:00-17:00; T7 08:00-11:00".
     */
    @Column(name = "working_schedule")
    private String workingSchedule;
}
