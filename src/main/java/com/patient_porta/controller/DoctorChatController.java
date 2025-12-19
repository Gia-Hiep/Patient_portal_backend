package com.patient_porta.controller;

import com.patient_porta.dto.*;
import com.patient_porta.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor-chat")
@RequiredArgsConstructor
public class DoctorChatController {

    private final ChatService chatService;

    @GetMapping("/patients")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<ChatPeerDTO>> patients(@RequestParam(value = "q", required = false) String q) {
        return ResponseEntity.ok(chatService.doctorPatients(q));
    }

    @GetMapping("/threads/{patientId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<ChatMessageDTO>> thread(@PathVariable Long patientId) {
        return ResponseEntity.ok(chatService.doctorThread(patientId));
    }

    @PostMapping("/threads/{patientId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ChatMessageDTO> send(@PathVariable Long patientId,
                                               @RequestBody SendMessageRequest req) {
        return ResponseEntity.ok(chatService.doctorSend(patientId, req));
    }
}
