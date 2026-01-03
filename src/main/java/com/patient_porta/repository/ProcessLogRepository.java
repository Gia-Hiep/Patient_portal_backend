
package com.patient_porta.repository;

import com.patient_porta.entity.ProcessLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProcessLogRepository extends JpaRepository<ProcessLog, Long> {
    Optional<ProcessLog> findTopByAppointmentIdOrderByCreatedAtDesc(Long appointmentId);
}


