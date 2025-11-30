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

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    @Enumerated(EnumType.STRING)
    private Status status; // CONFIRMED, COMPLETED, ...

    @Column(name = "department_name")
    private String departmentName; // nếu DB bạn chưa có, có thể bỏ hoặc map từ chỗ khác

    public enum Status {
        REQUESTED, CONFIRMED, COMPLETED, CANCELLED
    }
}
