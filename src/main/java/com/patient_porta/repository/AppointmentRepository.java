package com.patient_porta.repository;

import com.patient_porta.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("""
        select a from Appointment a
        where a.status = 'CONFIRMED'
          and a.scheduledAt between :from and :to
    """)
    List<Appointment> findAppointmentsBetween(LocalDateTime from, LocalDateTime to);
}
