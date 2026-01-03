package com.patient_porta.controller;

import com.patient_porta.dto.NotificationDTO;
import com.patient_porta.entity.Notification;
import com.patient_porta.entity.User;
import com.patient_porta.repository.NotificationRepository;
import com.patient_porta.repository.UserRepository;
import com.patient_porta.service.JwtService;
import com.patient_porta.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/autonotification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    // =========================
    // HELPER: LẤY USER TỪ JWT
    // =========================
    private User getUser(String token) {
        String username = jwtService.extractUsername(token.substring(7));

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // =========================
    // GET ALL NOTIFICATIONS
    // =========================
    @GetMapping
    public List<NotificationDTO> getAll(
            @RequestHeader("Authorization") String token
    ) {
        User user = getUser(token);

        // ⛔ Nếu user tắt auto notification → không trả gì
        if (!user.isAutoNotificationEnabled()) {
            return Collections.emptyList();
        }

        return notificationService.getNotifications(user.getId());
    }

    // =========================
    // MARK NOTIFICATION AS READ
    // =========================
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markRead(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id
    ) {
        User user = getUser(token);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        // ⛔ Bảo mật: không cho đọc notification của người khác
        if (!notification.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}
