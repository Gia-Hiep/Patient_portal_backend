package com.patient_porta.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class InvoiceSummaryDTO {
    private Long id;
    private String invoiceNo;
    private LocalDate issueDate;
    private BigDecimal totalAmount;
    private String status; // UNPAID/PAID/VOID
    // getters/setters
}

