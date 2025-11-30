
package com.patient_porta.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "process_logs")
@Data
public class ProcessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_id", nullable = false)
    private Long appointmentId;

    @Column(name = "stage_name", nullable = false, length = 255)
    private String stageName;

    @Column(name = "old_status")
    private String oldStatus;

    @Column(name = "new_status")
    private String newStatus;

    @Column(name = "updated_by")
    private Long updatedBy; // doctor_id

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
