package com.patient_porta.repository;

import com.patient_porta.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

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
=======
package com.patient_porta.repository;

import com.patient_porta.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Lấy lịch sử khám của 1 bệnh nhân, mới nhất trước
    List<Appointment> findByPatient_User_IdOrderByScheduledAtDesc(Long userId);

    // Lấy chi tiết 1 lần khám, nhưng phải thuộc về bệnh nhân có userId = ?
    Optional<Appointment> findByIdAndPatient_User_Id(Long id, Long userId);

    // Lấy lần khám mới nhất của 1 bệnh nhân (theo user_id)
    Optional<Appointment> findTopByPatient_User_IdOrderByScheduledAtDesc(Long userId);

    // lấy danh sách doctorId

    @Query("""
        select distinct a.doctor.userId
        from Appointment a
        where a.patient.userId = :patientId
    """)
    List<Long> findDistinctDoctorIdsByPatientId(@Param("patientId") Long patientId);

}

