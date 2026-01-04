package com.patient_porta.controller;

import com.patient_porta.config.JwtAuthenticationFilter;
import com.patient_porta.dto.admin.MedicalServiceDTO;
import com.patient_porta.service.MedicalServiceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = MedicalServiceController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = true)
@Import(TestSecurityConfig.class) // cho permitAll với /api/services
class MedicalServiceControllerTest {

    @Autowired MockMvc mvc;

    @MockitoBean MedicalServiceService service;

    private MedicalServiceDTO dto(long id, String code, String name, BigDecimal price) {
        MedicalServiceDTO d = new MedicalServiceDTO();
        d.setId(id);
        d.setCode(code);
        d.setName(name);
        d.setPrice(price);
        d.setActive(true);
        return d;
    }

    @Test
    void listActive_ok_public() throws Exception {
        when(service.listActive()).thenReturn(List.of(
                dto(1, "CONSULT", "Khám tư vấn", new BigDecimal("150000")),
                dto(2, "IMG_XRAY", "Chụp X-quang", new BigDecimal("350000"))
        ));

        mvc.perform(get("/api/services")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].active").value(true))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(service).listActive();
    }
}
