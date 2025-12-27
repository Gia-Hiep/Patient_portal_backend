package com.patient_porta.controller;

import com.patient_porta.entity.HospitalAnnouncement;
import com.patient_porta.service.HospitalAnnouncementService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/announcements")
@PreAuthorize("hasRole('ADMIN')")
public class HospitalAnnouncementAdminController {

    private final HospitalAnnouncementService service;

    public HospitalAnnouncementAdminController(HospitalAnnouncementService service) {
        this.service = service;
    }

    @PostMapping
    public HospitalAnnouncement create(@RequestBody HospitalAnnouncement a) {
        return service.create(a);
    }

    @PutMapping("/{id}")
    public HospitalAnnouncement update(
            @PathVariable Long id,
            @RequestBody HospitalAnnouncement a
    ) {
        return service.update(id, a);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
