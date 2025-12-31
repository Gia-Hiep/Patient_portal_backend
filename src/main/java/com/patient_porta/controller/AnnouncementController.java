package com.patient_porta.controller;

import com.patient_porta.entity.AnnouncementType;
import com.patient_porta.entity.HospitalAnnouncement;
import com.patient_porta.service.HospitalAnnouncementService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    private final HospitalAnnouncementService service;

    public AnnouncementController(HospitalAnnouncementService service) {
        this.service = service;
    }

    // PATIENT: chỉ xem + sắp xếp mới nhất + optional filter type
    @GetMapping
    public List<HospitalAnnouncement> list(
            @RequestParam(required = false) AnnouncementType type
    ) {
        return service.getForPatient(type);
    }
}
