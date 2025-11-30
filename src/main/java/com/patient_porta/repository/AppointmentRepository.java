package com.patient_porta.repository;

import com.patient_porta.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Đang dùng trong ProcessService
    Appointment findTopByPatientIdOrderByScheduledAtDesc(Long patientId);

    // 👉 Dùng cho AppointmentReminderScheduler
    @Query("""
           SELECT a 
           FROM Appointment a
           WHERE a.scheduledAt BETWEEN :from AND :to
           ORDER BY a.scheduledAt ASC
           """)
    List<Appointment> findAppointmentsBetween(@Param("from") LocalDateTime from,
                                              @Param("to") LocalDateTime to);
}
