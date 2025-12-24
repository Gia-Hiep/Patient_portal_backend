
package com.patient_porta.repository;

import com.patient_porta.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {


    Appointment findTopByPatientIdOrderByScheduledAtDesc(Long patientId);
    List<Appointment> findByStatus(String status);
    List<Appointment> findByDoctorIdOrderByScheduledAtAsc(Long doctorId);

}
