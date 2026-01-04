package com.patient_porta.controller;

import com.patient_porta.dto.admin.MedicalServiceDTO;
import com.patient_porta.dto.MedicalServiceUpsertRequest;
import com.patient_porta.service.MedicalServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/services")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminMedicalServiceController {

    private final MedicalServiceService service;

    @GetMapping
    public ResponseEntity<List<MedicalServiceDTO>> listAll() {
        return ResponseEntity.ok(service.adminListAll());
    }

    @PostMapping
    public ResponseEntity<MedicalServiceDTO> create(@RequestBody MedicalServiceUpsertRequest req) {
        return ResponseEntity.ok(service.adminCreate(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalServiceDTO> update(@PathVariable Long id,
                                                    @RequestBody MedicalServiceUpsertRequest req) {
        return ResponseEntity.ok(service.adminUpdate(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.adminDelete(id);
        return ResponseEntity.noContent().build();
    }
}
