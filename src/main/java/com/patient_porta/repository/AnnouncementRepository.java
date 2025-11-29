package com.patient_porta.repository;

import com.patient_porta.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    // mới nhất lên trên
    List<Announcement> findAllByOrderByPublishedAtDesc();
}
