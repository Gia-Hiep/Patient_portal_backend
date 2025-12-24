package com.patient_porta.controller;

import com.patient_porta.dto.NotificationDTO;
import com.patient_porta.entity.User;
import com.patient_porta.repository.UserRepository;
import com.patient_porta.service.JwtService;
import com.patient_porta.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    // =========================
    // HELPER: LẤY USER TỪ JWT
    // =========================
    private User getUserFromToken(String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // =========================
    // GET ALL NOTIFICATIONS (PATIENT)
    // =========================
    @GetMapping
    public List<NotificationDTO> getAll(
            @RequestHeader("Authorization") String authHeader
    ) {
        User user = getUserFromToken(authHeader);

        // ⛔ user tắt auto notification
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
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id
    ) {
        User user = getUserFromToken(authHeader);

        // 🔐 đảm bảo không đọc notification của người khác
        notificationService.markAsReadSecure(id, user.getId());

        return ResponseEntity.ok().build();
    }

    // =========================
    // SEND NOTIFICATION (US12)
    // =========================
    @PostMapping("/send")
    @PreAuthorize("hasAnyRole('DOCTOR','LAB_STAFF')")
    public ResponseEntity<?> sendNotification(
            @RequestBody NotificationDTO dto
    ) {
        if (dto.getPatientId() == null) {
            return ResponseEntity.badRequest()
                    .body("patientId is required");
        }

        notificationService.sendLabResultNotification(
                dto.getPatientId(),
                dto.getBody()
        );

        return ResponseEntity.ok().build();
    }

    // ====================================================
    // 🔥 BỔ SUNG – GET SETTING AUTO NOTIFICATION
    // ====================================================
    @GetMapping("/setting")
    public ResponseEntity<?> getAutoNotificationSetting(
            @RequestHeader("Authorization") String authHeader
    ) {
        User user = getUserFromToken(authHeader);

        return ResponseEntity.ok(
                Collections.singletonMap(
                        "enabled",
                        user.isAutoNotificationEnabled()
                )
        );
    }

    // ====================================================
    // 🔥 BỔ SUNG – UPDATE SETTING → GHI DB
    // ====================================================
    @PutMapping("/setting")
    public ResponseEntity<?> updateAutoNotificationSetting(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody java.util.Map<String, Boolean> body
    ) {
        User user = getUserFromToken(authHeader);

        Boolean enabled = body.get("enabled");
        if (enabled == null) {
            return ResponseEntity.badRequest().body("enabled is required");
        }

        user.setAutoNotificationEnabled(enabled);
        userRepository.save(user); // ✅ GHI DB

        return ResponseEntity.ok().build();
    }
}
