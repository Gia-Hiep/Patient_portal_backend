package com.patient_porta.dto;

import java.time.LocalDateTime;

public class DoctorAppointmentResponse {

    private String patientName;
    private LocalDateTime scheduledAt;
    private String status;

    public DoctorAppointmentResponse() {
    }

    public DoctorAppointmentResponse(String patientName, LocalDateTime scheduledAt, String status) {
        this.patientName = patientName;
        this.scheduledAt = scheduledAt;
        this.status = status;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
