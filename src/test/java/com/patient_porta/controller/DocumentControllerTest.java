package com.patient_porta.controller;

import com.patient_porta.service.DocumentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.startsWith;

@WebMvcTest(controllers = DocumentController.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = {DocumentController.class, DocumentControllerTest.TestConfig.class})
class DocumentControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired DocumentService documentService; // mock

    @Configuration
    static class TestConfig {
        @Bean
        DocumentService documentService() {
            return mock(DocumentService.class);
        }
    }

    @TestConfiguration
    @EnableMethodSecurity // giữ lại, nhưng mình chặn ngay từ HTTP security cho chắc
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable());
            http.authorizeHttpRequests(auth -> auth
                    // 👇 CHẶN quyền theo URL: chỉ PATIENT mới được gọi API tài liệu
                    .requestMatchers("/api/documents/**").hasRole("PATIENT")
                    .anyRequest().authenticated()
            );
            return http.build();
        }
    }





    @Test
    @DisplayName("GET /api/documents/{id}/view -> 200 inline PDF")
    @WithMockUser(username = "patient01", roles = {"PATIENT"})
    void view_inline_ok() throws Exception {
        long docId = 5L;
        byte[] content = "%PDF-1.4 ...".getBytes();
        Resource body = new ByteArrayResource(content);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, APPLICATION_PDF_VALUE);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"HoaDon.pdf\"");

        when(documentService.streamPdf(docId, false))
                .thenReturn(ResponseEntity.ok().headers(headers).body(body));

        mockMvc.perform(get("/api/documents/{id}/view", docId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, APPLICATION_PDF_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, startsWith("inline")))
                .andExpect(content().bytes(content));
    }

    @Test
    @DisplayName("GET /api/documents/{id}/download -> 200 attachment PDF")
    @WithMockUser(username = "patient01", roles = {"PATIENT"})
    void download_attachment_ok() throws Exception {
        long docId = 7L;
        byte[] content = "PDFDATA".getBytes();
        Resource body = new ByteArrayResource(content);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, APPLICATION_PDF_VALUE);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Inv007.pdf\"");

        when(documentService.streamPdf(docId, true))
                .thenReturn(ResponseEntity.ok().headers(headers).body(body));

        mockMvc.perform(get("/api/documents/{id}/download", docId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, startsWith("attachment")))
                .andExpect(content().bytes(content));
    }

    @Test
    @DisplayName("GET /api/documents/{id}/view -> 404")
    @WithMockUser(username = "patient01", roles = {"PATIENT"})
    void view_notFound() throws Exception {
        when(documentService.streamPdf(99L, false))
                .thenThrow(new org.springframework.web.server.ResponseStatusException(NOT_FOUND, "not ready"));

        mockMvc.perform(get("/api/documents/{id}/view", 99L))
                .andExpect(status().isNotFound());
    }
}
