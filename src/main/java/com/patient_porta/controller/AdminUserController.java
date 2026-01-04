package com.patient_porta.controller;

import com.patient_porta.dto.admin.*;
import com.patient_porta.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService service;

    @GetMapping
    public ResponseEntity<List<AdminUserDTO>> list() {
        return ResponseEntity.ok(service.listUsers());
    }

    @PostMapping
    public ResponseEntity<AdminUserDTO> create(@RequestBody AdminUserCreateRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<AdminUserDTO> role(
            @PathVariable Long id,
            @RequestBody AdminRoleUpdateRequest req) {
        return ResponseEntity.ok(service.changeRole(id, req));
    }

    @PutMapping("/{id}/lock")
    public ResponseEntity<AdminUserDTO> lock(@PathVariable Long id) {
        return ResponseEntity.ok(service.lock(id));
    }

    @PutMapping("/{id}/unlock")
    public ResponseEntity<AdminUserDTO> unlock(@PathVariable Long id) {
        return ResponseEntity.ok(service.unlock(id));
    }
}
