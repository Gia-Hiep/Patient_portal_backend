package com.patient_porta.repository;

import com.patient_porta.entity.Document;
import com.patient_porta.entity.Document.DocType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    // Lấy các PDF LAB/IMAGING gắn với 1 lần khám
    List<Document> findByAppointment_IdAndDocTypeIn(Long appointmentId, List<DocType> types);
}
