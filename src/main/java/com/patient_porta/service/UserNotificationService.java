package com.patient_porta.service;

import com.patient_porta.entity.User;
import com.patient_porta.entity.UserNotification;
import com.patient_porta.entity.UserNotification.Status;
import com.patient_porta.repository.UserNotificationRepository;
import com.patient_porta.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserNotificationService {

    private final UserNotificationRepository notificationRepository;
    private final UserRepository userRepository;

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found for notifications"));
        return user.getId();
    }

    public List<UserNotification> getMyNotifications() {
        Long userId = getCurrentUserId();
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void markAsRead(Long id) {
        Long userId = getCurrentUserId();
        UserNotification notif = notificationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        if (notif.getStatus() == Status.UNREAD) {
            notif.setStatus(Status.READ);
            notificationRepository.save(notif);
        }
    }

    public long getUnreadCount() {
        Long userId = getCurrentUserId();
        return notificationRepository.countByUserIdAndStatus(userId, Status.UNREAD);
    }
}
