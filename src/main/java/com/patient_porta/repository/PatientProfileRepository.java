package com.patient_porta.repository;

import com.patient_porta.entity.PatientProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientProfileRepository extends JpaRepository<PatientProfile, Long> {

}
