package com.patient_porta.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MedicalServiceUpsertRequest {
    private String name;
    private String description;
    private BigDecimal price;
}
