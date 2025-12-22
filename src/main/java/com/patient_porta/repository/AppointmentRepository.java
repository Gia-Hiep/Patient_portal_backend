package com.patient_porta.repository;

import com.patient_porta.entity.Appointment;
import com.patient_porta.entity.Appointment.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // ================= DOCTOR – Dashboard (US9) =================

    List<Appointment> findByDoctor_UserIdAndScheduledAtBetweenOrderByScheduledAtAsc(
            Long doctorUserId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Appointment> findByDoctor_UserIdAndScheduledAtBetweenAndStatusInOrderByScheduledAtAsc(
            Long doctorUserId,
            LocalDateTime start,
            LocalDateTime end,
            List<Appointment.Status> statuses
    );

    // ================= PATIENT =================

    List<Appointment> findByPatient_User_IdOrderByScheduledAtDesc(Long userId);

    Optional<Appointment> findByIdAndPatient_User_Id(Long appointmentId, Long userId);

    Optional<Appointment> findTopByPatient_User_IdOrderByScheduledAtDesc(Long userId);
}
