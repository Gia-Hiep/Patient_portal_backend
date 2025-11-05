package com.patient_porta.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // dùng Long để khớp BIGINT

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password_hash;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // PATIENT/DOCTOR/ADMIN

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status; // ACTIVE/LOCKED/DISABLED

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updated_at;

    public enum Role {
        PATIENT, DOCTOR, ADMIN
    }

    public enum Status {
        ACTIVE, LOCKED, DISABLED
    }
}
