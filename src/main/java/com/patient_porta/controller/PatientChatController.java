package com.patient_porta.controller;

import com.patient_porta.dto.*;
import com.patient_porta.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class PatientChatController {

    private final ChatService chatService;

    @GetMapping("/doctors")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<ChatPeerDTO>> myDoctors() {
        return ResponseEntity.ok(chatService.myDoctors());
    }

    @GetMapping("/threads/{doctorId}")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<ChatMessageDTO>> thread(@PathVariable Long doctorId) {
        return ResponseEntity.ok(chatService.patientThread(doctorId));
    }

    @PostMapping("/threads/{doctorId}")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ChatMessageDTO> send(@PathVariable Long doctorId,
                                               @RequestBody SendMessageRequest req) {
        return ResponseEntity.ok(chatService.patientSend(doctorId, req));
    }
}
