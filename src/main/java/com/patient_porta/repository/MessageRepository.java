package com.patient_porta.repository;

import com.patient_porta.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByPatientIdAndDoctorIdOrderBySentAtAsc(Long patientId, Long doctorId);

    // Doctor: list bệnh nhân đã chat
    @Query("""
      select m.patientId
      from Message m
      where m.doctorId = :doctorId
      group by m.patientId
      order by max(m.sentAt) desc
    """)
    List<Long> findPatientIdsChattedWithDoctor(Long doctorId);
}
