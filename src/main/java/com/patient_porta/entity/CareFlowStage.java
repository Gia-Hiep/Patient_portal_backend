
package com.patient_porta.entity;

import jakarta.persistence.*;
<<<<<<< HEAD
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@AllArgsConstructor
@Entity
@Table(name = "care_flow_stages")
@Data
@NoArgsConstructor
=======
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "care_flow_stages")
@Data
>>>>>>> 1e942ba8dd7557adf7e5387bce14233c8be0740a
public class CareFlowStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_id", nullable = false)
    private Long appointmentId;

    @Column(name = "stage_order", nullable = false)
    private Integer stageOrder;

    @Column(name = "stage_name", nullable = false, length = 128)
    private String stageName;

    @Column(nullable = false)
    private String status; // NOT_STARTED, WAITING, IN_PROGRESS, DONE, CANCELLED

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
<<<<<<< HEAD

    public CareFlowStage(Long id, Integer stageOrder, String stageName) {
        this.id = id;
        this.stageOrder = stageOrder;
        this.stageName = stageName;
    }
}
=======
}
>>>>>>> 1e942ba8dd7557adf7e5387bce14233c8be0740a
