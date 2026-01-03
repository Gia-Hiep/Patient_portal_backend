package com.patient_porta.controller;

import com.patient_porta.entity.UserNotification;
import com.patient_porta.service.UserNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class UserNotificationController {

    private final UserNotificationService service;

    /**
     * Scrum-51: API: Lấy danh sách thông báo /api/notifications (BE)
     */
    @GetMapping
    public ResponseEntity<List<UserNotification>> listMyNotifications() {
        return ResponseEntity.ok(service.getMyNotifications());
    }

    /**
     * Scrum-52: API: Đánh dấu đã đọc /api/notifications/{id}/read (BE)
     */
    @PostMapping("/{id}/read")
    public ResponseEntity<?> markRead(@PathVariable Long id) {
        service.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    /**
     * (Bonus) Lấy số lượng thông báo chưa đọc để làm badge 🔔
     */
    @GetMapping("/unread-count")
    public ResponseEntity<?> unreadCount() {
        long count = service.getUnreadCount();
        return ResponseEntity.ok().body(count);
    }
}
