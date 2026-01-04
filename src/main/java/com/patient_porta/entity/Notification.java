package com.patient_porta.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // USER NHẬN THÔNG BÁO
    // =========================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // =========================
    // PATIENT (PHỤC VỤ US12)
    // =========================
    @Column(name = "patient_id")
    private Long patientId;

    // =========================
    // META
    // =========================
    private String type;          // LAB_RESULT, SYSTEM, ...

    private String title;

    @Column(columnDefinition = "varchar(255)")
    private String body;

    // =========================
    // TRẠNG THÁI
    // =========================
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;   // UNREAD / READ

    public enum Status {
        UNREAD, READ
    }

    // =========================
    // READ FLAG (DB BẮT BUỘC)
    // =========================
    @Column(name = "read_flag", nullable = false)
    private boolean readFlag;

    // =========================
    // THỜI GIAN
    // =========================
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // =========================
    // LIÊN KẾT NGỮ CẢNH
    // =========================
    @Column(name = "related_type")
    private String relatedType;

    @Column(name = "related_id")
    private Long relatedId;

    // =========================
    // AUTO SET KHI INSERT
    // =========================
    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = Status.UNREAD;
        }
        // read_flag DB NOT NULL
        // nếu chưa set → false
    }
}
