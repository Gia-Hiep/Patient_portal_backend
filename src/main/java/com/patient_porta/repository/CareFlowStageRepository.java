
package com.patient_porta.repository;

import com.patient_porta.entity.CareFlowStage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareFlowStageRepository extends JpaRepository<CareFlowStage, Long> {


    List<CareFlowStage> findAllByOrderByStageOrderAsc();
}
