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

@WebMvcTest(controllers = PatientChatController.class)
@AutoConfigureMockMvc(addFilters = false) // tắt filter (JwtAuthenticationFilter, v.v.)
class PatientChatControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockitoBean ChatService chatService;


    @MockitoBean JwtService jwtService;

    @Test
    @WithMockUser(roles = "PATIENT")
    void myDoctors_ok() throws Exception {
        Mockito.when(chatService.myDoctors()).thenReturn(List.of());

        mvc.perform(get("/api/chat/doctors"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void thread_ok() throws Exception {
        Mockito.when(chatService.patientThread(eq(20L))).thenReturn(List.of());

        mvc.perform(get("/api/chat/threads/{doctorId}", 20))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void send_ok() throws Exception {
        SendMessageRequest req = new SendMessageRequest();
        req.setContent("hi doctor");

        ChatMessageDTO resp = new ChatMessageDTO();

        Mockito.when(chatService.patientSend(eq(20L), any(SendMessageRequest.class)))
                .thenReturn(resp);

        mvc.perform(post("/api/chat/threads/{doctorId}", 20)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

}
