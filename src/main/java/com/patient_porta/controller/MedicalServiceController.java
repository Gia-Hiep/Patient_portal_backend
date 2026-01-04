package com.patient_porta.controller;

import com.patient_porta.dto.admin.MedicalServiceDTO;
import com.patient_porta.service.MedicalServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Bệnh nhân/Bác sĩ xem danh sách dịch vụ (chỉ active)
 */
@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class MedicalServiceController {

    private final MedicalServiceService service;

    @GetMapping
    public ResponseEntity<List<MedicalServiceDTO>> listActive() {
        return ResponseEntity.ok(service.listActive());
    }
}
