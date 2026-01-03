package com.patient_porta.repository;

import com.patient_porta.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // ================= PATIENT =================

    // Lấy lịch sử khám của 1 bệnh nhân, mới nhất trước
    List<Appointment> findByPatient_UserIdOrderByScheduledAtDesc(Long patientUserId);

    // Lấy chi tiết 1 lần khám, nhưng phải thuộc về bệnh nhân có userId = ?
    Optional<Appointment> findByIdAndPatient_UserId(Long id, Long patientUserId);

    // Lấy lần khám mới nhất của 1 bệnh nhân (theo user_id)
    Optional<Appointment> findTopByPatient_UserIdOrderByScheduledAtDesc(Long patientUserId);

    // ================= DOCTOR =================

    // Lấy lịch của doctor (tăng dần theo thời gian)
    List<Appointment> findByDoctor_UserIdOrderByScheduledAtAsc(Long doctorUserId);

    // Dashboard/US9: lịch trong khoảng thời gian
    List<Appointment> findByDoctor_UserIdAndScheduledAtBetweenOrderByScheduledAtAsc(
            Long doctorUserId,
            LocalDateTime start,
            LocalDateTime end
    );

    // Dashboard/US9: lịch trong khoảng thời gian + lọc nhiều status
    List<Appointment> findByDoctor_UserIdAndScheduledAtBetweenAndStatusInOrderByScheduledAtAsc(
            Long doctorUserId,
            LocalDateTime start,
            LocalDateTime end,
            List<Appointment.Status> statuses
    );

    // ================= COMMON QUERIES =================

    @Query("""
            SELECT a
            FROM Appointment a
            WHERE a.scheduledAt BETWEEN :from AND :to
            ORDER BY a.scheduledAt ASC
            """)
    List<Appointment> findAppointmentsBetween(@Param("from") LocalDateTime from,
                                              @Param("to") LocalDateTime to);

    @Query("""
      select distinct a.doctor.userId
      from Appointment a
      where a.patient.userId = :patientUserId
    """)
    List<Long> findDistinctDoctorIdsByPatientUserId(@Param("patientUserId") Long patientUserId);

    // Nếu cần lọc theo status -> dùng enum chuẩn
    List<Appointment> findByStatus(Appointment.Status status);
}
