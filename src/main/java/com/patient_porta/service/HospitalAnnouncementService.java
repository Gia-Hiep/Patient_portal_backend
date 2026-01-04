package com.patient_porta.service;

import com.patient_porta.entity.AnnouncementType;
import com.patient_porta.entity.HospitalAnnouncement;
import com.patient_porta.exception.ResourceNotFoundException;
import com.patient_porta.repository.HospitalAnnouncementRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class HospitalAnnouncementService {

    private final HospitalAnnouncementRepository repo;

    public HospitalAnnouncementService(HospitalAnnouncementRepository repo) {
        this.repo = repo;
    }

    public HospitalAnnouncement create(HospitalAnnouncement a) {
        validate(a);
        return repo.save(a);
    }

    public HospitalAnnouncement update(Long id, HospitalAnnouncement a) {
        validate(a);
        HospitalAnnouncement existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông báo"));

        existing.setTitle(a.getTitle());
        existing.setContent(a.getContent());
        existing.setType(a.getType());

        return repo.save(existing);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy thông báo");
        }
        repo.deleteById(id);
    }

    public List<HospitalAnnouncement> getForPatient(AnnouncementType type) {
        return type == null
                ? repo.findAllByOrderByCreatedAtDesc()
                : repo.findByTypeOrderByCreatedAtDesc(type);
    }

    private void validate(HospitalAnnouncement a) {
        if (!StringUtils.hasText(a.getTitle())
                || !StringUtils.hasText(a.getContent())
                || a.getType() == null) {
            throw new IllegalArgumentException("Vui lòng nhập đầy đủ thông tin.");
        }
    }
}