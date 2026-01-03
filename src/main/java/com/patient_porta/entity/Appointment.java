
package com.patient_porta.entity;

import jakarta.persistence.*;
        import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // patient_id -> patient_profiles.user_id
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientProfile patient;

    // doctor_id -> doctor_profiles.user_id
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private DoctorProfile doctor;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private MedicalService service;

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "notes")
    private String notes;

    public enum Status {
        REQUESTED, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW, IN_PROGRESS
    }

    @Column(name = "current_stage_id")
    private Long currentStageId;
}