package com.patient_porta.repository;

import com.patient_porta.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByPatientIdOrderByIssueDateDesc(Long patientId);
    Optional<Invoice> findByIdAndPatientId(Long id, Long patientId);
}