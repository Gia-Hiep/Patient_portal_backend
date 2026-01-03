package com.patient_porta.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
public class UserNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // user nhận thông báo
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type; // LAB_READY, APPT_REMINDER, REVISIT_REMINDER, SYSTEM, QUEUE_CALL

    @Column(nullable = false, length = 191)
    private String title;

    @Column(name = "body", nullable = false, length = 512)
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status; // UNREAD, READ

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "related_type", nullable = false)
    private RelatedType relatedType; // APPOINTMENT, DOCUMENT, INVOICE, NONE

    @Column(name = "related_id")
    private Long relatedId;

    public enum Type {
        LAB_READY, APPT_REMINDER, REVISIT_REMINDER, SYSTEM, QUEUE_CALL
    }

    public enum Status {
        UNREAD, READ
    }

    public enum RelatedType {
        APPOINTMENT, DOCUMENT, INVOICE, NONE
    }
}
