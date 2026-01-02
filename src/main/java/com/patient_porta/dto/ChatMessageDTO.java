package com.patient_porta.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class ChatMessageDTO {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private Long senderUserId;
    private String senderRole; // PATIENT/DOCTOR
    private String content;
    private Instant sentAt;
}
