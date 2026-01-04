package com.patient_porta.service;

import com.patient_porta.dto.DoctorAppointmentResponse;
import com.patient_porta.entity.Appointment;
import com.patient_porta.entity.Appointment.Status;
import com.patient_porta.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Service
public class DoctorAppointmentService {

    private final AppointmentRepository appointmentRepo;

    public DoctorAppointmentService(AppointmentRepository appointmentRepo) {
        this.appointmentRepo = appointmentRepo;
    }

    public List<DoctorAppointmentResponse> getAppointmentsForDoctor(
            Long doctorUserId,
            String filter,
            String date
    ) {
        LocalDate targetDate =
                (date == null || date.isBlank())
                        ? LocalDate.now()
                        : LocalDate.parse(date);

        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = targetDate.plusDays(1).atStartOfDay();

        List<Appointment> apps;

        if (filter == null || filter.isBlank()) {
            apps = appointmentRepo
                    .findByDoctor_UserIdAndScheduledAtBetweenOrderByScheduledAtAsc(
                            doctorUserId, start, end
                    );
        } else {
            List<Appointment.Status> statuses = mapFilter(filter);
            if (statuses.isEmpty()) return List.of();

            apps = appointmentRepo
                    .findByDoctor_UserIdAndScheduledAtBetweenAndStatusInOrderByScheduledAtAsc(
                            doctorUserId, start, end, statuses
                    );
        }

        return apps.stream()
                .map(a -> new DoctorAppointmentResponse(
                        a.getPatient().getFullName(),
                        a.getScheduledAt(),
                        a.getStatus().name()
                ))
                .toList();
    }

    private List<Appointment.Status> mapFilter(String filter) {
        return switch (filter) {
            case "WAITING" -> List.of(
                    Appointment.Status.REQUESTED,
                    Appointment.Status.CONFIRMED
            );
            case "DONE" -> List.of(Appointment.Status.COMPLETED);
            case "CANCELLED" -> List.of(
                    Appointment.Status.CANCELLED,
                    Appointment.Status.NO_SHOW
            );
            default -> List.of();
        };
    }
}
