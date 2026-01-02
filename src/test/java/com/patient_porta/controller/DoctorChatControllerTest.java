package com.patient_porta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.patient_porta.dto.ChatMessageDTO;
import com.patient_porta.dto.SendMessageRequest;
import com.patient_porta.service.ChatService;
import com.patient_porta.service.JwtService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DoctorChatController.class)
@AutoConfigureMockMvc(addFilters = false) // tắt filter (JwtAuthenticationFilter, v.v.)
class DoctorChatControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockitoBean
    ChatService chatService;


    @MockitoBean JwtService jwtService;

    @Test
    @WithMockUser(roles = "DOCTOR")
    void patients_ok_noQuery() throws Exception {
        Mockito.when(chatService.doctorPatients(isNull())).thenReturn(List.of());

        mvc.perform(get("/api/doctor-chat/patients"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void patients_ok_withQuery() throws Exception {
        Mockito.when(chatService.doctorPatients(eq("an"))).thenReturn(List.of());

        mvc.perform(get("/api/doctor-chat/patients").param("q", "an"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void thread_ok() throws Exception {
        Mockito.when(chatService.doctorThread(eq(10L))).thenReturn(List.of());

        mvc.perform(get("/api/doctor-chat/threads/{patientId}", 10))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void send_ok() throws Exception {
        SendMessageRequest req = new SendMessageRequest();
        req.setContent("hello");

        // Nếu ChatMessageDTO của bạn không có default constructor thì đổi sang object thật phù hợp
        ChatMessageDTO resp = new ChatMessageDTO();

        Mockito.when(chatService.doctorSend(eq(10L), any(SendMessageRequest.class)))
                .thenReturn(resp);

        mvc.perform(post("/api/doctor-chat/threads/{patientId}", 10)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}
