package com.patient_porta.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table(name = "invoices")
@Data
public class Invoice {
    public enum Status { UNPAID, PAID, VOID }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_id", nullable = false)
    private Long patientId; // FK patient_profiles.user_id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @Column(name = "invoice_no", nullable = false, unique = true)
    private String invoiceNo;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "items_json", columnDefinition = "json", nullable = false)
    private String itemsJson; // [{code,name,qty,price},...]

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

}