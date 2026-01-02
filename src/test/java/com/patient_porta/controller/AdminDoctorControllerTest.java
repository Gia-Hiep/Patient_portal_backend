package com.patient_porta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.patient_porta.dto.CreateDoctorRequest;
import com.patient_porta.dto.DoctorAdminDTO;
import com.patient_porta.dto.UpdateDoctorRequest;
import com.patient_porta.service.AdminDoctorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdminDoctorControllerTest {

    @Mock
    private AdminDoctorService adminDoctorService;

    private MockMvc mvc;
    private ObjectMapper om;

    @BeforeEach
    void setUp() {
        this.mvc = MockMvcBuilders
                .standaloneSetup(new AdminDoctorController(adminDoctorService))
                .build();
        this.om = new ObjectMapper();
    }

    @Test
    void list_shouldReturnArray() throws Exception {
        DoctorAdminDTO dto = new DoctorAdminDTO();
        dto.setId(29L);
        dto.setUsername("lego");
        dto.setEmail("concac@gmail.com");
        dto.setStatus("ACTIVE");
        dto.setFullName("Bác sĩ Lê Văn B");

        when(adminDoctorService.listDoctors(false)).thenReturn(List.of(dto));

        mvc.perform(get("/api/admin/doctors")
                        .param("includeDisabled", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(29));

        verify(adminDoctorService).listDoctors(false);
    }

    @Test
    void update_shouldReturnUpdatedDto() throws Exception {
        UpdateDoctorRequest req = new UpdateDoctorRequest();
        req.setFullName("BS Updated");
        req.setSpecialty("Nội");
        req.setDepartment("Khám tổng quát");
        req.setLicenseNo("C1");
        req.setBio("bio");
        req.setWorkingSchedule("T2-T6");

        DoctorAdminDTO dto = new DoctorAdminDTO();
        dto.setId(8L);
        dto.setFullName("BS Updated");
        dto.setSpecialty("Nội");

        when(adminDoctorService.updateDoctor(eq(8L), any(UpdateDoctorRequest.class)))
                .thenReturn(dto);

        mvc.perform(put("/api/admin/doctors/8")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(8))
                .andExpect(jsonPath("$.fullName").value("BS Updated"));

        verify(adminDoctorService).updateDoctor(eq(8L), any(UpdateDoctorRequest.class));
    }

    @Test
    void delete_shouldReturnSuccessJson() throws Exception {
        doNothing().when(adminDoctorService).disableDoctor(11L);

        mvc.perform(delete("/api/admin/doctors/11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(adminDoctorService).disableDoctor(11L);
    }

    @Test
    void create_shouldReturnCreatedDto() throws Exception {
        CreateDoctorRequest req = new CreateDoctorRequest();
        req.setUsername("newdoc");
        req.setEmail("newdoc@example.com");
        req.setPassword("123456");
        req.setFullName("BS New");

        DoctorAdminDTO dto = new DoctorAdminDTO();
        dto.setId(99L);
        dto.setUsername("newdoc");
        dto.setEmail("newdoc@example.com");
        dto.setFullName("BS New");
        dto.setStatus("ACTIVE");

        when(adminDoctorService.createDoctor(any(CreateDoctorRequest.class))).thenReturn(dto);

        mvc.perform(post("/api/admin/doctors")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.username").value("newdoc"));

        verify(adminDoctorService).createDoctor(any(CreateDoctorRequest.class));
    }
}
