package com.patient_porta.controller;

import com.patient_porta.entity.BackupHistory;
import com.patient_porta.service.BackupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/backups")
public class BackupAdminController {

    private final BackupService service;

    public BackupAdminController(BackupService service) {
        this.service = service;
    }

    // scrum 180: thực hiện backup
    @PostMapping
    public Map<String, Object> backupNow() {
        String fileName = service.performBackup();
        return Map.of(
                "message", "Backup thành công!",
                "fileName", fileName
        );
    }

    // scrum 181: lấy danh sách file backup
    @GetMapping("/files")
    public List<String> listFiles() {
        return service.listBackupFiles();
    }

    // scrum 182: lịch sử backup
    @GetMapping("/history")
    public List<BackupHistory> history() {
        return service.getHistory();
    }
}
