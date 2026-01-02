package com.patient_porta.controller;

import com.patient_porta.entity.User;
import com.patient_porta.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me/settings")
@RequiredArgsConstructor
public class UserSettingsController {

    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/notifications")
    public ResponseEntity<NotificationSettingsDTO> getNotificationSettings() {
        User user = getCurrentUser();
        NotificationSettingsDTO dto = new NotificationSettingsDTO();
        dto.setAutoNotifyEnabled(user.isAutoNotificationEnabled());
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/notifications")
    public ResponseEntity<NotificationSettingsDTO> updateNotificationSettings(
            @RequestBody NotificationSettingsDTO dto
    ) {
        User user = getCurrentUser();
        user.setAutoNotificationEnabled(dto.isAutoNotifyEnabled());
        userRepository.save(user);
        return ResponseEntity.ok(dto);
    }

    @Data
    public static class NotificationSettingsDTO {
        private boolean autoNotifyEnabled;
    }
}
    