package com.patient_porta.service;

import com.patient_porta.entity.HospitalAnnouncement;
import com.patient_porta.repository.HospitalAnnouncementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HospitalAnnouncementService {

    private final HospitalAnnouncementRepository repo;

    public HospitalAnnouncementService(HospitalAnnouncementRepository repo) {
        this.repo = repo;
    }

    public HospitalAnnouncement create(HospitalAnnouncement a) {
        if (a.getTitle() == null || a.getTitle().trim().isEmpty()
                || a.getContent() == null || a.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập đầy đủ thông tin.");
        }
        return repo.save(a);
    }

    public HospitalAnnouncement update(Long id, HospitalAnnouncement data) {
        HospitalAnnouncement a = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông báo"));

        a.setTitle(data.getTitle());
        a.setContent(data.getContent());
        a.setType(data.getType());

        return repo.save(a);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public List<HospitalAnnouncement> getAll() {
        return repo.findAllByOrderByCreatedAtDesc();
    }
}
