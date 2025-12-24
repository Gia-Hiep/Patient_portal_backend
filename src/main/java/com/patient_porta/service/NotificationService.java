package com.patient_porta.service;

import com.patient_porta.dto.NotificationDTO;
import com.patient_porta.entity.Notification;
import com.patient_porta.entity.User;
import com.patient_porta.repository.NotificationRepository;
import com.patient_porta.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repo;
    private final UserRepository userRepository;

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
    // ĐÁNH DẤU ĐÃ ĐỌC (CŨ)
    // =========================
    public void markAsRead(Long id) {
        repo.findById(id).ifPresent(n -> {
            if (n.getStatus() != Notification.Status.READ) {
                n.setStatus(Notification.Status.READ);
                n.setReadFlag(true); // ✅ BẮT BUỘC
                repo.save(n);
            }
        });
    }

    // =========================
    // 🔐 ĐÁNH DẤU ĐÃ ĐỌC (AN TOÀN)
    // =========================
    public void markAsReadSecure(Long notificationId, Long userId) {

        Notification n = repo.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!n.getUser().getId().equals(userId)) {
            throw new RuntimeException("Forbidden");
        }

        if (n.getStatus() != Notification.Status.READ) {
            n.setStatus(Notification.Status.READ);
            n.setReadFlag(true); // ✅ BẮT BUỘC
            repo.save(n);
        }
    }

    // =========================
    // ✅ GỬI THÔNG BÁO KQXN (US12)
    // =========================
    public void sendLabResultNotification(Long patientId, String content) {

        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bệnh nhân"));

        Notification n = new Notification();

        // 🔥 BẮT BUỘC – KHỚP DB
        n.setUser(patient);                 // user_id
        n.setPatientId(patientId);          // patient_id
        n.setReadFlag(false);               // read_flag NOT NULL

        // 🔥 NỘI DUNG
        n.setTitle("Kết quả xét nghiệm");
        n.setBody(content);
        n.setStatus(Notification.Status.UNREAD);

        // (OPTIONAL – nếu entity có)
        // n.setType("LAB_RESULT");
        // n.setMessage(content);

        repo.save(n); // ✅ KHÔNG CÒN 500
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
                .readFlag(n.isReadFlag())
                .createdAt(n.getCreatedAt())
                .patientId(n.getPatientId())
                .build();
    }
}
