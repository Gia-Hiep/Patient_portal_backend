package com.patient_porta.service;

import com.patient_porta.dto.ProfileDTO;
import com.patient_porta.entity.PatientProfile;
import com.patient_porta.entity.User;
import com.patient_porta.repository.PatientProfileRepository;
import com.patient_porta.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final PatientProfileRepository patientProfileRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public ProfileDTO getMyProfile() {
        User user = getCurrentUser();
        if (user.getRole() != User.Role.PATIENT) {
            throw new RuntimeException("Chỉ bệnh nhân mới xem được hồ sơ cá nhân dạng này");
        }

        PatientProfile profile = patientProfileRepository
                .findById(user.getId())
                .orElseGet(() -> createDefaultProfileForUser(user));

        return toDTO(user, profile);
    }

    public ProfileDTO updateMyProfile(ProfileDTO dto) {
        User user = getCurrentUser();
        if (user.getRole() != User.Role.PATIENT) {
            throw new RuntimeException("Chỉ bệnh nhân mới được cập nhật hồ sơ cá nhân");
        }

        PatientProfile profile = patientProfileRepository
                .findById(user.getId())
                .orElseGet(() -> createDefaultProfileForUser(user));

        profile.setFullName(dto.getFullName());
        profile.setDateOfBirth(dto.getDateOfBirth());
        profile.setAddress(dto.getAddress());
        profile.setInsuranceNumber(dto.getInsuranceNumber());
        profile.setEmergencyContactName(dto.getEmergencyContactName());
        profile.setEmergencyContactPhone(dto.getEmergencyContactPhone());

        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());

        patientProfileRepository.save(profile);
        userRepository.save(user);

        return toDTO(user, profile);
    }

    private PatientProfile createDefaultProfileForUser(User user) {
        PatientProfile p = new PatientProfile();
        p.setUser(user);
        p.setUserId(user.getId());
        p.setFullName(user.getUsername());
        return patientProfileRepository.save(p);
    }

    private ProfileDTO toDTO(User user, PatientProfile profile) {
        ProfileDTO dto = new ProfileDTO();
        dto.setFullName(profile.getFullName());
        dto.setDateOfBirth(profile.getDateOfBirth());
        dto.setAddress(profile.getAddress());
        dto.setInsuranceNumber(profile.getInsuranceNumber());
        dto.setEmergencyContactName(profile.getEmergencyContactName());
        dto.setEmergencyContactPhone(profile.getEmergencyContactPhone());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        return dto;
    }
}
