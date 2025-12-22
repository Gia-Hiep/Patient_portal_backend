package com.patient_porta.controller;

import com.patient_porta.dto.DoctorAppointmentResponse;
import com.patient_porta.entity.User;
import com.patient_porta.repository.UserRepository;
import com.patient_porta.service.DoctorAppointmentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/doctor/appointments")
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorAppointmentController {

    private final DoctorAppointmentService service;
    private final UserRepository userRepository;

    public DoctorAppointmentController(
            DoctorAppointmentService service,
            UserRepository userRepository
    ) {
        this.service = service;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<DoctorAppointmentResponse> getAppointments(
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "date", required = false) String date,
            Principal principal
    ) {
        if (principal == null) {
            throw new RuntimeException("Unauthenticated request");
        }

        String username = principal.getName(); // doctor01

        Long doctorUserId = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Doctor not found: " + username)
                )
                .getId();

        return service.getAppointmentsForDoctor(
                doctorUserId,
                status,
                date
        );
    }
}
