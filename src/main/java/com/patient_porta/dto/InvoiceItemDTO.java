package com.patient_porta.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvoiceItemDTO {
    public String code;
    public String name;
    public Integer qty;
    public BigDecimal price;
}
