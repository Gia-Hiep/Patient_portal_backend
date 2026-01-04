package com.patient_porta.controller;

import com.patient_porta.dto.DoctorAdminDTO;
import com.patient_porta.dto.UpdateDoctorRequest;
import com.patient_porta.service.AdminDoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.patient_porta.dto.CreateDoctorRequest;

import java.util.List;
import java.util.Map;

/**
 * US14.2 - Quản lý danh sách bác sĩ (Admin)
 */
@RestController
@RequestMapping("/api/admin/doctors")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDoctorController {

    private final AdminDoctorService adminDoctorService;

    /**
     * API: Lấy danh sách bác sĩ
     * GET /api/admin/doctors?includeDisabled=true|false
     */
    @GetMapping
    public List<DoctorAdminDTO> list(@RequestParam(defaultValue = "false") boolean includeDisabled) {
        return adminDoctorService.listDoctors(includeDisabled);
    }

    /**
     * API: Cập nhật thông tin bác sĩ (chuyên khoa, lịch làm việc, ...)
     * PUT /api/admin/doctors/{doctorId}
     */
    @PutMapping("/{doctorId}")
    public DoctorAdminDTO update(@PathVariable Long doctorId, @RequestBody UpdateDoctorRequest req) {
        return adminDoctorService.updateDoctor(doctorId, req);
    }

    /**
     * API: Xóa bác sĩ (soft delete -> DISABLED)
     * DELETE /api/admin/doctors/{doctorId}
     */
    @DeleteMapping("/{doctorId}")
    public Map<String, Object> delete(@PathVariable Long doctorId) {
        adminDoctorService.disableDoctor(doctorId);
        return Map.of("success", true, "message", "Đã vô hiệu hóa tài khoản bác sĩ");
    }

    /**
     * API: Tạo mới bác sĩ
     * POST /api/admin/doctors
     */
    @PostMapping
    public DoctorAdminDTO create(@RequestBody CreateDoctorRequest req) {
        return adminDoctorService.createDoctor(req);
    }

}
