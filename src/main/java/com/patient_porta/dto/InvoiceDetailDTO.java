package com.patient_porta.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class InvoiceDetailDTO {
    private Long id;
    private String invoiceNo;
    private LocalDate issueDate;
    private List<InvoiceItemDTO> items;
    private BigDecimal totalAmount;
    private String status;
    // Link PDF nếu đã có document INVOICE
    private Long documentId; // nullable
    private String viewUrl;  // /api/documents/{id}/view
    private String downloadUrl; // /api/documents/{id}/download

}
