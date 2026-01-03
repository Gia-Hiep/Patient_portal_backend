package com.patient_porta.controller;

import com.patient_porta.dto.AutoNotificationSettingDTO;
import com.patient_porta.entity.User;
import com.patient_porta.repository.UserRepository;
import com.patient_porta.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class UserSettingController {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    private User getMe(String token) {
        String username = jwtService.extractUsername(token.substring(7));
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // =====================
    // GET SETTING
    // =====================
    @GetMapping("/auto-notifications")
    public AutoNotificationSettingDTO getSetting(
            @RequestHeader("Authorization") String token
    ) {
        User me = getMe(token);
        return new AutoNotificationSettingDTO(me.isAutoNotificationEnabled());
    }

    // =====================
    // UPDATE SETTING
    // =====================
    @PutMapping("/auto-notifications")
    public ResponseEntity<?> updateSetting(
            @RequestHeader("Authorization") String token,
            @RequestBody AutoNotificationSettingDTO body
    ) {
        User me = getMe(token);
        me.setAutoNotificationEnabled(body.isEnabled());
        userRepository.save(me);
        return ResponseEntity.ok().build();
    }
}
