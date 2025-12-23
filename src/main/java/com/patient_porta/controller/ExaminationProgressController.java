    package com.patient_porta.controller;

    import com.patient_porta.dto.UpdateCareFlowStageDTO;
    import com.patient_porta.entity.User;
    import com.patient_porta.repository.UserRepository;
    import com.patient_porta.service.ExaminationProgressService;
    import com.patient_porta.service.JwtService;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/api/examination-progress")
    @RequiredArgsConstructor
    public class ExaminationProgressController {

        private final ExaminationProgressService examinationProgressService;


        private final JwtService jwtService;
        private final UserRepository userRepo;

        // =========================
        // 🔐 LẤY DOCTOR TỪ TOKEN
        // =========================
        private User getDoctor(String authHeader) {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new SecurityException("Token không hợp lệ");
            }

            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);

            User user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            if (user.getRole() != User.Role.DOCTOR) {
                throw new SecurityException("Bạn không có quyền thực hiện thao tác này.");
            }

            return user;
        }
        @GetMapping
        public ResponseEntity<?> getPatientsForDoctor(
                @RequestHeader("Authorization") String authHeader
        ) {
            User doctor = getDoctor(authHeader);

            return ResponseEntity.ok(
                    examinationProgressService.getPatientsForDoctor(doctor.getId())
            );
        }
        // ======================================================
        // 👨‍⚕️ BÁC SĨ CẬP NHẬT TRẠNG THÁI THEO PATIENT ID
        // ======================================================
        @PutMapping("/patient/{patientId}")
        public ResponseEntity<?> updateStageByPatient(
                @RequestHeader("Authorization") String authHeader,
                @PathVariable Long patientId,
                @RequestBody UpdateCareFlowStageDTO body
        ) {
            User doctor = getDoctor(authHeader);

            if (body.getStageId() == null) {
                return ResponseEntity.badRequest()
                        .body("Vui lòng chọn trạng thái để cập nhật.");
            }

            examinationProgressService.updateStageByPatient(
                    patientId,
                    body.getStageId(),
                    doctor
            );

            return ResponseEntity.ok("Cập nhật trạng thái thành công.");
        }
    }
