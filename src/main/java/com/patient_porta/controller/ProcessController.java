
package com.patient_porta.controller;

import com.patient_porta.entity.User;
import com.patient_porta.repository.UserRepository;
import com.patient_porta.service.ProcessService;
import com.patient_porta.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProcessController {

    private final UserRepository userRepository;
    private final ProcessService processService;

    @GetMapping("/process")
    public ResponseEntity<?> getProcessStatus() {

        System.out.println("\n[PROCESS] Bắt đầu xử lý yêu cầu /api/process");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            System.out.println("[PROCESS] Lỗi: Authentication rỗng");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthenticated");
        }

        String username = auth.getName();
        System.out.println("[PROCESS] Username từ JWT: " + username);

        User user = userRepository.findByUsername(username)
                .orElse(null);

        if (user == null) {
            System.out.println("[PROCESS] Lỗi: User không tồn tại!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        System.out.println("[PROCESS] userId=" + user.getId() + " | role=" + user.getRole());

        if (user.getRole() != User.Role.PATIENT) {
            System.out.println("[PROCESS] Quyền truy cập bị từ chối: chỉ bệnh nhân được phép xem.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only patient can view process status");
        }

        List<CareFlowStageDTO> stages = processService.getProcessForPatient(user.getId());


        System.out.println("[PROCESS] Hoàn thành trả kết quả cho FE.\n");

        ProcessResponseDTO res = new ProcessResponseDTO();
    res.setAppointmentId(user.getId());
    res.setStages(stages);
return ResponseEntity.ok(res);
    }
}
