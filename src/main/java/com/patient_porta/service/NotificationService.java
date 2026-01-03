package com.patient_porta.service;

import com.patient_porta.dto.NotificationDTO;
import com.patient_porta.entity.Notification;
import com.patient_porta.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repo;

    // =========================
    // LẤY DS THÔNG BÁO CỦA USER
    // =========================
    public List<NotificationDTO> getNotifications(Long userId) {

        return repo.findByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // =========================
    // ĐÁNH DẤU ĐÃ ĐỌC
    // =========================
    public void markAsRead(Long id) {
        repo.findById(id).ifPresent(n -> {
            if (n.getStatus() != Notification.Status.READ) {
                n.setStatus(Notification.Status.READ);
                repo.save(n);
            }
        });
    }

    // =========================
    // MAP ENTITY → DTO
    // =========================
    private NotificationDTO toDTO(Notification n) {
        return NotificationDTO.builder()
                .id(n.getId())
                .title(n.getTitle())
                .body(n.getBody())
                .status(n.getStatus().name())
                .readFlag(n.getStatus() == Notification.Status.READ)
                .createdAt(n.getCreatedAt())
                .build();
    }
}
