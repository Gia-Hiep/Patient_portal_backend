package com.patient_porta.dto.admin;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MedicalServiceDTO {
    private Long id;
    private String code;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean active;
}
