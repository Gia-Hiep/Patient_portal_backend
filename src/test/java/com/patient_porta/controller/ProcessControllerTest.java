package com.patient_porta.controller;

import com.patient_porta.entity.User;
import com.patient_porta.repository.UserRepository;
import com.patient_porta.service.ProcessService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProcessController.class)
class ProcessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
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
        doctor.setRole(User.Role.DOCTOR);

        when(userRepository.findByUsername("doctor1"))
                .thenReturn(Optional.of(doctor));

        mockMvc.perform(get("/api/process"))
                .andExpect(status().isForbidden());
    }
}
