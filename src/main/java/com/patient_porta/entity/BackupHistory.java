package com.patient_porta.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "backup_history")
public class BackupHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime backupTime;

    @Column(nullable = false)
    private String status; // SUCCESS / FAILED

    @Column(nullable = false)
    private String fileName;

    public BackupHistory() {}

    public BackupHistory(LocalDateTime backupTime, String status, String fileName) {
        this.backupTime = backupTime;
        this.status = status;
        this.fileName = fileName;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getBackupTime() {
        return backupTime;
    }

    public String getStatus() {
        return status;
    }

    public String getFileName() {
        return fileName;
    }

    public void setBackupTime(LocalDateTime backupTime) {
        this.backupTime = backupTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
