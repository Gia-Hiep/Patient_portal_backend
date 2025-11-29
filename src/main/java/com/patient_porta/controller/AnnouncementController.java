package com.patient_porta.controller;

import com.patient_porta.entity.Announcement;
import com.patient_porta.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService service;

    @GetMapping
    public ResponseEntity<List<Announcement>> list() {
        return ResponseEntity.ok(service.getAll());
    }
}
