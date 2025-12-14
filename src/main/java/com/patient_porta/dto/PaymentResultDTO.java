package com.patient_porta.dto;

import lombok.Data;

@Data
public class PaymentResultDTO {
    private String outcome; // SUCCESS/FAIL
    private String message;
    private String newStatus; // PAID/UNPAID
}
