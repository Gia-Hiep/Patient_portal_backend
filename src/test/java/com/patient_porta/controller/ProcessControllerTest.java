package com.patient_porta.controller;

import com.patient_porta.config.JwtAuthenticationFilter;
import com.patient_porta.entity.User;
import com.patient_porta.repository.UserRepository;
import com.patient_porta.service.ProcessService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ProcessController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
class ProcessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private ProcessService processService;

    @Test
    @WithMockUser(username = "patient1")
    void patientCanViewProcess() throws Exception {
        User patient = new User();
        patient.setId(10L);
        patient.setRole(User.Role.PATIENT);

        when(userRepository.findByUsername("patient1"))
                .thenReturn(Optional.of(patient));

        when(processService.getProcessForPatient(10L))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/process"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "doctor1")
    void doctorCannotViewProcess() throws Exception {
        User doctor = new User();
        doctor.setId(20L);
        doctor.setRole(User.Role.DOCTOR);

        when(userRepository.findByUsername("doctor1"))
                .thenReturn(Optional.of(doctor));

        mockMvc.perform(get("/api/process"))
                .andExpect(status().isForbidden());
    }
}
