package com.patient_porta.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Data
@Entity
@Table(name = "messages")
public class Message {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_id", nullable = false)
    private Long patientId; // patient_profiles.user_id

    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;  // doctor_profiles.user_id

    @Column(name = "sender_user_id", nullable = false)
    private Long senderUserId; // users.id

    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;

    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;

    @Column(name = "read_at")
    private java.time.LocalDateTime readAt;
}
