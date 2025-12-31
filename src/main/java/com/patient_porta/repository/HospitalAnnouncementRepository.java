package com.patient_porta.repository;
import com.patient_porta.entity.AnnouncementType;
import com.patient_porta.entity.HospitalAnnouncement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HospitalAnnouncementRepository
        extends JpaRepository<HospitalAnnouncement, Long> {

    List<HospitalAnnouncement> findAllByOrderByCreatedAtDesc();

    List<HospitalAnnouncement> findByTypeOrderByCreatedAtDesc(AnnouncementType type);
}
