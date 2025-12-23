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


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String type;

    private String title;

    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;   // UNREAD / READ

    public enum Status {
        UNREAD, READ
    }
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "related_type")
    private String relatedType;

    @Column(name = "related_id")
    private Long relatedId;
}
