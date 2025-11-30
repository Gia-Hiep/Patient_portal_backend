package com.patient_porta.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "announcements")
@Data
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Level level; // NEWS / URGENT

    public enum Level {
        NEWS, URGENT
    }

    @Column(nullable = false, length = 191)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "author_user_id")
    private Long authorUserId;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;
}
