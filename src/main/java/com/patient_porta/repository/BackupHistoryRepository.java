package com.patient_porta.repository;

import com.patient_porta.entity.BackupHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BackupHistoryRepository extends JpaRepository<BackupHistory, Long> {
    List<BackupHistory> findAllByOrderByBackupTimeDesc();
}
