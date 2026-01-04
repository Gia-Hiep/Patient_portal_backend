package com.patient_porta.service;

import com.patient_porta.dto.CreateDoctorRequest;
import com.patient_porta.dto.DoctorAdminDTO;
import com.patient_porta.dto.UpdateDoctorRequest;
import com.patient_porta.entity.DoctorProfile;
import com.patient_porta.entity.User;
import com.patient_porta.repository.DoctorProfileRepository;
import com.patient_porta.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * US14.2 - Admin quản lý danh sách bác sĩ
 * - Xem danh sách
 * - Cập nhật thông tin
 * - Vô hiệu hóa (soft delete)
 * - Tạo mới bác sĩ
 */
@Service
@RequiredArgsConstructor
public class AdminDoctorService {

    private final UserRepository userRepo;
    private final DoctorProfileRepository doctorProfileRepo;
    private final PasswordEncoder passwordEncoder;


    /* ====================== LIST ====================== */

    public List<DoctorAdminDTO> listDoctors(boolean includeDisabled) {
        List<User> doctors = includeDisabled
                ? userRepo.findAllByRole(User.Role.DOCTOR)
                : userRepo.findAllByRoleAndStatusNot(
                User.Role.DOCTOR,
                User.Status.DISABLED
        );

        return doctors.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /* ====================== CREATE ====================== */

    /**
     * Tạo mới bác sĩ (Admin)
     * - Tạo User role = DOCTOR
     * - Tạo DoctorProfile
     * - Password mặc định: 123456 (nếu không truyền)
     */
    @Transactional
    public DoctorAdminDTO createDoctor(CreateDoctorRequest req) {
        if (req.getUsername() == null || req.getUsername().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Username không được để trống"
            );
        }
        if (req.getEmail() == null || req.getEmail().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email không được để trống"
            );
        }

        if (userRepo.existsByUsername(req.getUsername())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Username đã tồn tại"
            );
        }
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email đã tồn tại"
            );
        }

        /* ---- Create User ---- */
        User u = new User();
        u.setUsername(req.getUsername().trim());
        u.setEmail(req.getEmail().trim());
        u.setRole(User.Role.DOCTOR);
        u.setStatus(User.Status.ACTIVE);

        String rawPassword =
                (req.getPassword() == null || req.getPassword().isBlank())
                        ? "123456"
                        : req.getPassword();

        u.setPassword_hash(passwordEncoder.encode(rawPassword));

        userRepo.save(u);

        /* ---- Create DoctorProfile ---- */
        DoctorProfile p = new DoctorProfile();
        p.setUser(u); // @MapsId => tự map user_id
        p.setFullName(
                req.getFullName() != null && !req.getFullName().isBlank()
                        ? req.getFullName().trim()
                        : u.getUsername()
        );
        p.setSpecialty(blankToNull(req.getSpecialty()));
        p.setDepartment(blankToNull(req.getDepartment()));
        p.setLicenseNo(blankToNull(req.getLicenseNo()));
        p.setWorkingSchedule(blankToNull(req.getWorkingSchedule()));

        doctorProfileRepo.save(p);

        return toDto(u, p);
    }

    /* ====================== UPDATE ====================== */

    @Transactional
    public DoctorAdminDTO updateDoctor(Long doctorId, UpdateDoctorRequest req) {
        User u = userRepo.findById(doctorId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Không tìm thấy bác sĩ"
                        )
                );

        if (u.getRole() != User.Role.DOCTOR) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User này không phải bác sĩ"
            );
        }

        DoctorProfile profile = doctorProfileRepo
                .findById(doctorId)
                .orElseGet(() -> {
                    DoctorProfile p = new DoctorProfile();
                    p.setUser(u); // @MapsId
                    return p;
                });

        // fullName bắt buộc (nullable=false)
        if (req.getFullName() != null && !req.getFullName().trim().isEmpty()) {
            profile.setFullName(req.getFullName().trim());
        } else if (profile.getFullName() == null
                || profile.getFullName().trim().isEmpty()) {
            profile.setFullName(u.getUsername());
        }

        if (req.getSpecialty() != null)
            profile.setSpecialty(blankToNull(req.getSpecialty()));
        if (req.getDepartment() != null)
            profile.setDepartment(blankToNull(req.getDepartment()));
        if (req.getLicenseNo() != null)
            profile.setLicenseNo(blankToNull(req.getLicenseNo()));
        if (req.getBio() != null)
            profile.setBio(blankToNull(req.getBio()));
        if (req.getWorkingSchedule() != null)
            profile.setWorkingSchedule(blankToNull(req.getWorkingSchedule()));

        DoctorProfile saved = doctorProfileRepo.save(profile);
        return toDto(u, saved);
    }

    /* ====================== DELETE (SOFT) ====================== */

    /**
     * Vô hiệu hóa bác sĩ (soft delete)
     */
    @Transactional
    public void disableDoctor(Long doctorId) {
        User u = userRepo.findById(doctorId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Không tìm thấy bác sĩ"
                        )
                );

        if (u.getRole() != User.Role.DOCTOR) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User này không phải bác sĩ"
            );
        }

        u.setStatus(User.Status.DISABLED);
        userRepo.save(u);
    }

    /* ====================== MAPPING HELPERS ====================== */

    private DoctorAdminDTO toDto(User u) {
        DoctorProfile p =
                doctorProfileRepo.findById(u.getId()).orElse(null);
        return toDto(u, p);
    }

    private DoctorAdminDTO toDto(User u, DoctorProfile p) {
        DoctorAdminDTO d = new DoctorAdminDTO();
        d.setId(u.getId());
        d.setUsername(u.getUsername());
        d.setEmail(u.getEmail());
        d.setStatus(
                u.getStatus() != null ? u.getStatus().name() : null
        );

        if (p != null) {
            d.setFullName(p.getFullName());
            d.setSpecialty(p.getSpecialty());
            d.setDepartment(p.getDepartment());
            d.setLicenseNo(p.getLicenseNo());
            d.setBio(p.getBio());
            d.setWorkingSchedule(p.getWorkingSchedule());
        }
        return d;
    }

    private String blankToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
