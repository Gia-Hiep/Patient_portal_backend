package com.patient_porta.controller;

import com.patient_porta.entity.HospitalAnnouncement;
import com.patient_porta.service.HospitalAnnouncementService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RestController
@RequestMapping("/api/announcements")
public class HospitalAnnouncementController {

    private final HospitalAnnouncementService service;

    public HospitalAnnouncementController(HospitalAnnouncementService service) {
        this.service = service;
    }

    @GetMapping
    public List<HospitalAnnouncement> list() {
        return service.getAll();
    }
}
