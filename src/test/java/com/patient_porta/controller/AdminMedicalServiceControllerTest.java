package com.patient_porta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.patient_porta.config.JwtAuthenticationFilter;
import com.patient_porta.dto.admin.MedicalServiceDTO;
import com.patient_porta.dto.MedicalServiceUpsertRequest;
import com.patient_porta.service.MedicalServiceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AdminMedicalServiceController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = true)
@Import(TestSecurityConfig.class)
class AdminMedicalServiceControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockitoBean MedicalServiceService service;

    private MedicalServiceDTO dto(long id, String code, String name, String desc, BigDecimal price, boolean active) {
        MedicalServiceDTO d = new MedicalServiceDTO();
        d.setId(id);
        d.setCode(code);
        d.setName(name);
        d.setDescription(desc);
        d.setPrice(price);
        d.setActive(active);
        return d;
    }

    @Test
    void listAll_unauthorized_whenNoUser() throws Exception {
        mvc.perform(get("/api/admin/services")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(service);
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void listAll_forbidden_whenWrongRole() throws Exception {
        mvc.perform(get("/api/admin/services")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        verifyNoInteractions(service);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listAll_ok_whenAdmin() throws Exception {
        when(service.adminListAll()).thenReturn(List.of(
                dto(1, "CONSULT", "Khám tư vấn", null, new BigDecimal("150000"), true),
                dto(2, "LAB_BLOOD", "Xét nghiệm máu", null, new BigDecimal("200000"), false)
        ));

        mvc.perform(get("/api/admin/services")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].code").value("CONSULT"))
                .andExpect(jsonPath("$[0].active").value(true))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].active").value(false));

        verify(service).adminListAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ok_whenAdmin() throws Exception {
        MedicalServiceUpsertRequest req = new MedicalServiceUpsertRequest();
        req.setName("Chụp X-quang");
        req.setDescription("Chụp X-quang trong cơ thể");
        req.setPrice(new BigDecimal("350000"));

        when(service.adminCreate(any(MedicalServiceUpsertRequest.class)))
                .thenReturn(dto(10, "IMG_XRAY_ABC123", "Chụp X-quang", "Chụp X-quang trong cơ thể", new BigDecimal("350000"), true));

        mvc.perform(post("/api/admin/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Chụp X-quang"))
                .andExpect(jsonPath("$.active").value(true));

        verify(service).adminCreate(any(MedicalServiceUpsertRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_badRequest_whenServiceThrows400() throws Exception {
        MedicalServiceUpsertRequest req = new MedicalServiceUpsertRequest();
        req.setName("ABC");
        req.setDescription("...");
        req.setPrice(new BigDecimal("-1"));

        when(service.adminCreate(any(MedicalServiceUpsertRequest.class)))
                .thenThrow(new ResponseStatusException(BAD_REQUEST, "Giá không hợp lệ."));

        mvc.perform(post("/api/admin/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verify(service).adminCreate(any(MedicalServiceUpsertRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ok_whenAdmin() throws Exception {
        MedicalServiceUpsertRequest req = new MedicalServiceUpsertRequest();
        req.setName("Khám tư vấn");
        req.setDescription("Update");
        req.setPrice(new BigDecimal("200000"));

        when(service.adminUpdate(eq(1L), any(MedicalServiceUpsertRequest.class)))
                .thenReturn(dto(1, "CONSULT", "Khám tư vấn", "Update", new BigDecimal("200000"), true));

        mvc.perform(put("/api/admin/services/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Update"))
                .andExpect(jsonPath("$.price").value(200000));

        verify(service).adminUpdate(eq(1L), any(MedicalServiceUpsertRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_notFound_whenServiceThrows404() throws Exception {
        MedicalServiceUpsertRequest req = new MedicalServiceUpsertRequest();
        req.setName("ABC");
        req.setDescription("DEF");
        req.setPrice(new BigDecimal("1"));

        when(service.adminUpdate(eq(999L), any(MedicalServiceUpsertRequest.class)))
                .thenThrow(new ResponseStatusException(NOT_FOUND, "Không tìm thấy dịch vụ"));

        mvc.perform(put("/api/admin/services/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isNotFound());

        verify(service).adminUpdate(eq(999L), any(MedicalServiceUpsertRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_noContent_whenAdmin() throws Exception {
        doNothing().when(service).adminDelete(1L);

        mvc.perform(delete("/api/admin/services/1"))
                .andExpect(status().isNoContent());

        verify(service).adminDelete(1L);
    }
}
