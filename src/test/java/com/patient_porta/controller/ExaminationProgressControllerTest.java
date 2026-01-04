package com.patient_porta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.patient_porta.dto.UpdateCareFlowStageDTO;
import com.patient_porta.entity.User;
import com.patient_porta.repository.UserRepository;
import com.patient_porta.service.ExaminationProgressService;
import com.patient_porta.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExaminationProgressController.class)
@AutoConfigureMockMvc(addFilters = false) // ✅ tắt Security/JWT filter để không bị lỗi load context
class ExaminationProgressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExaminationProgressService examinationProgressService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String TOKEN = "Bearer mock-token";

    private User mockDoctor() {
        User doctor = new User();
        doctor.setId(1L);
        doctor.setRole(User.Role.DOCTOR);
        doctor.setUsername("doctor1");
        return doctor;
    }

    // =============================
    // ✅ GET /api/examination-progress
    // =============================
    @Test
    void getPatientsForDoctor_success() throws Exception {
        User doctor = mockDoctor();

        when(jwtService.extractUsername("mock-token")).thenReturn("doctor1");
        when(userRepository.findByUsername("doctor1")).thenReturn(Optional.of(doctor));

        when(examinationProgressService.getPatientsForDoctor(1L))
                .thenReturn(List.of(
                        Map.of("patientId", 24L, "fullName", "Nguyễn Văn A")
                ));

        mockMvc.perform(get("/api/examination-progress")
                        .header("Authorization", TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].patientId").value(24));

        verify(examinationProgressService).getPatientsForDoctor(1L);
    }

    // =============================
    // ✅ PUT /api/examination-progress/patient/{id}
    // =============================
    @Test
    void updateStageByPatient_success() throws Exception {
        User doctor = mockDoctor();

        UpdateCareFlowStageDTO dto = new UpdateCareFlowStageDTO();
        dto.setStageId(2L);

        when(jwtService.extractUsername("mock-token")).thenReturn("doctor1");
        when(userRepository.findByUsername("doctor1")).thenReturn(Optional.of(doctor));

        doNothing().when(examinationProgressService)
                .updateStageByPatient(24L, 2L, doctor);

        mockMvc.perform(put("/api/examination-progress/patient/24")
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Cập nhật trạng thái thành công."));

        verify(examinationProgressService).updateStageByPatient(24L, 2L, doctor);
    }

    // =============================
    // ❌ PUT thiếu stageId -> 400
    // =============================
    @Test
    void updateStageByPatient_missingStageId() throws Exception {
        User doctor = mockDoctor();

        when(jwtService.extractUsername("mock-token")).thenReturn("doctor1");
        when(userRepository.findByUsername("doctor1")).thenReturn(Optional.of(doctor));

        mockMvc.perform(put("/api/examination-progress/patient/24")
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(examinationProgressService, never()).updateStageByPatient(anyLong(), anyLong(), any());
    }
}
