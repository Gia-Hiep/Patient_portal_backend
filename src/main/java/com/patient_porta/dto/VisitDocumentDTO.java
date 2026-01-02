package com.patient_porta.dto;

import lombok.Data;

@Data
public class VisitDocumentDTO {
    private Long id;
    private String type;        // LAB / IMAGING / ...
    private String title;
    private String mimeType;
    private String viewUrl;     // link xem PDF
    private String downloadUrl; // link tải PDF
}
