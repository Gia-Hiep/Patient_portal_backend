package com.patient_porta.service;

import com.patient_porta.dto.admin.*;
import com.patient_porta.entity.User;
import com.patient_porta.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    private User me() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    private void requireAdmin(User u) {
        if (u.getRole() != User.Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Chỉ admin được phép");
        }
    }

    /* ===== LIST ===== */
    public List<AdminUserDTO> listUsers() {
        requireAdmin(me());
        return userRepo.findAll().stream().map(this::toDto).toList();
    }

    /* ===== CREATE ===== */
    @Transactional
    public AdminUserDTO create(AdminUserCreateRequest req) {
        requireAdmin(me());

        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email đã tồn tại trong hệ thống."
            );
        }
        if(userRepo.findByUsername(req.getUsername()).isPresent()){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User name đã tồn tại. "
            );
        }

        User u = new User();
        u.setUsername(req.getUsername());
        u.setEmail(req.getEmail());
        u.setPhone(req.getPhone());
        u.setPassword_hash(passwordEncoder.encode(req.getPassword()));
        u.setRole(User.Role.valueOf(req.getRole()));
        u.setStatus(User.Status.ACTIVE);

        return toDto(userRepo.save(u));
    }

    /* ===== CHANGE ROLE ===== */
    @Transactional
    public AdminUserDTO changeRole(Long id, AdminRoleUpdateRequest req) {
        requireAdmin(me());

        User u = userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        u.setRole(User.Role.valueOf(req.getRole()));
        return toDto(userRepo.save(u));
    }

    /* ===== LOCK / UNLOCK ===== */
    @Transactional
    public AdminUserDTO lock(Long id) {
        requireAdmin(me());
        User u = userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        u.setStatus(User.Status.LOCKED);
        return toDto(userRepo.save(u));
    }

    @Transactional
    public AdminUserDTO unlock(Long id) {
        requireAdmin(me());
        User u = userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        u.setStatus(User.Status.ACTIVE);
        return toDto(userRepo.save(u));
    }

    private AdminUserDTO toDto(User u) {
        AdminUserDTO d = new AdminUserDTO();
        d.setId(u.getId());
        d.setUsername(u.getUsername());
        d.setEmail(u.getEmail());
        d.setPhone(u.getPhone());
        d.setRole(u.getRole().name());
        d.setStatus(u.getStatus().name());
        return d;
    }
}
