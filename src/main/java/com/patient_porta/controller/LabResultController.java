package com.patient_porta.controller;

import com.patient_porta.dto.LabResultDetailDTO;
import com.patient_porta.dto.LabResultPatientDTO;
import com.patient_porta.service.LabResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lab-results")
@RequiredArgsConstructor
public class LabResultController {

    private final LabResultService labResultService;

    // =====================================
    // US12 – DS bệnh nhân có KQ xét nghiệm
    // =====================================
    @GetMapping("/patients")
    @PreAuthorize("hasAnyRole('DOCTOR','LAB_STAFF')")
    public List<LabResultPatientDTO> getPatientsWithLabResult() {
        return labResultService.getPatientsWithLabResult();
    }

    // =====================================
    // US12 – Chi tiết kết quả xét nghiệm
    // =====================================
    @GetMapping("/{patientId}")
    @PreAuthorize("hasAnyRole('DOCTOR','LAB_STAFF')")
    public LabResultDetailDTO getLabResultDetail(
            @PathVariable Long patientId
    ) {
        return labResultService.getLabResultDetail(patientId);
    }
}
