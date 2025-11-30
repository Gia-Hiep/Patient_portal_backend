
package com.patient_porta.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProcessResponseDTO {
    private Long appointmentId;
    private List<CareFlowStageDTO> stages;
}
