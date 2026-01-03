
package com.patient_porta.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@AllArgsConstructor
@Entity
@Table(name = "care_flow_stages")
@Data
@NoArgsConstructor
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

    public CareFlowStage(Long id, Integer stageOrder, String stageName) {
        this.id = id;
        this.stageOrder = stageOrder;
        this.stageName = stageName;
    }
}
