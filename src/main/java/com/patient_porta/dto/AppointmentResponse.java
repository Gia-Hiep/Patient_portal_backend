package com.patient_porta.dto;

import com.patient_porta.entity.AppointmentStatus;
import java.time.LocalDateTime;

public class AppointmentResponse {

    private String patientName;
    private LocalDateTime appointmentTime;
    private AppointmentStatus status;

    public AppointmentResponse() {}

    public AppointmentResponse(String patientName, LocalDateTime appointmentTime, AppointmentStatus status) {
        this.patientName = patientName;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }
}
