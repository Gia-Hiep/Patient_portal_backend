package com.patient_porta.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder
public class NotificationDTO {
    private Long id;
    private String title;
    private String body;
    private String status;
    private LocalDateTime createdAt;
    private boolean readFlag;
    private Long patientId;
}


