package com.patient_porta.repository;

import com.patient_porta.entity.MedicalService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicalServiceRepository extends JpaRepository<MedicalService, Long> {

    List<MedicalService> findByActiveTrueOrderByNameAsc();

    Optional<MedicalService> findByCode(String code);

    boolean existsByCode(String code);
}
