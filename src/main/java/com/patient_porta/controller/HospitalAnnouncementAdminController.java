package com.patient_porta.controller;

import com.patient_porta.dto.ApiResponse;
import com.patient_porta.entity.HospitalAnnouncement;
import com.patient_porta.service.HospitalAnnouncementService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/announcements")
public class HospitalAnnouncementAdminController {

    private final HospitalAnnouncementService service;

    public HospitalAnnouncementAdminController(HospitalAnnouncementService service) {
        this.service = service;
    }

    @PostMapping
    public ApiResponse create(@RequestBody HospitalAnnouncement a) {
        service.create(a);
        return new ApiResponse("Tạo mới thành công.");
    }

    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable Long id,
                              @RequestBody HospitalAnnouncement a) {
        service.update(id, a);
        return new ApiResponse("Cập nhật thành công.");
    }

    @DeleteMapping("/{id}")
    public ApiResponse delete(@PathVariable Long id) {
        service.delete(id);
        return new ApiResponse("Xóa thành công.");
    }
}
