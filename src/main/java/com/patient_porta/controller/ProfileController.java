package com.patient_porta.controller;

import com.patient_porta.dto.ProfileDTO;
import com.patient_porta.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<ProfileDTO> getMyProfile() {
        return ResponseEntity.ok(profileService.getMyProfile());
    }

    @PutMapping("/me")
    public ResponseEntity<ProfileDTO> updateMyProfile(@RequestBody ProfileDTO dto) {
        ProfileDTO updated = profileService.updateMyProfile(dto);
        return ResponseEntity.ok(updated);
    }
}
