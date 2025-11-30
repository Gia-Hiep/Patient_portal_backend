package com.patient_porta.service;

import com.patient_porta.entity.Announcement;
import com.patient_porta.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository repo;

    public List<Announcement> getAll() {
        return repo.findAllByOrderByPublishedAtDesc();
    }
}
