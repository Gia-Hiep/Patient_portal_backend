package com.patient_porta.repository;

import com.patient_porta.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Lấy lịch sử khám của 1 bệnh nhân, mới nhất trước
    List<Appointment> findByPatient_User_IdOrderByScheduledAtDesc(Long userId);

    // Lấy chi tiết 1 lần khám, nhưng phải thuộc về bệnh nhân có userId = ?
    Optional<Appointment> findByIdAndPatient_User_Id(Long id, Long userId);

    // Lấy lần khám mới nhất của 1 bệnh nhân (theo user_id)
    Optional<Appointment> findTopByPatient_User_IdOrderByScheduledAtDesc(Long userId);

    @Query("""
            SELECT a
            FROM Appointment a
            WHERE a.scheduledAt BETWEEN :from AND :to
            ORDER BY a.scheduledAt ASC
            """)
    List<Appointment> findAppointmentsBetween(@Param("from") LocalDateTime from,
                                              @Param("to") LocalDateTime to);


    Optional<Appointment> findTopByPatient_UserIdOrderByScheduledAtDesc(Long patientUserId);

    List<Appointment> findByDoctor_UserIdOrderByScheduledAtAsc(Long doctorUserId);

    @Query("""
      select distinct a.doctor.userId
      from Appointment a
      where a.patient.userId = :patientUserId
    """)
    List<Long> findDistinctDoctorIdsByPatientUserId(@Param("patientUserId") Long patientUserId);

    // nếu bạn vẫn cần lọc theo status enum (khuyên dùng enum thay vì String)
    // List<Appointment> findByStatus(Appointment.Status status);
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
