package com.patient_porta.service;
import java.nio.file.Path;
import com.patient_porta.entity.BackupHistory;
import com.patient_porta.repository.BackupHistoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BackupService {

    private final BackupHistoryRepository historyRepo;

    // đổi đường dẫn bằng application.properties: backup.dir=backups
    @Value("${backup.dir:backups}")
    private String backupDir;

    public BackupService(BackupHistoryRepository historyRepo) {
        this.historyRepo = historyRepo;
    }

    // scrum 179 + 180: tạo file backup theo format file_backup_dd_MM_yyyy
    public String performBackup() {
        String fileName = buildBackupFileName();

        try {
            Path dir = Path.of(backupDir);

            // giả lập lỗi nếu đường dẫn không khả dụng (nếu backupDir trỏ tới file chứ không phải folder)
            if (Files.exists(dir) && !Files.isDirectory(dir)) {
                throw new IllegalStateException("Backup path is not a directory");
            }

            Files.createDirectories(dir);

            File file = dir.resolve(fileName).toFile();

            // giả lập nội dung backup
            try (FileWriter w = new FileWriter(file)) {
                w.write("FAKE BACKUP\n");
                w.write("time=" + LocalDateTime.now() + "\n");
                w.write("note=file generated for US16\n");
            }

            historyRepo.save(new BackupHistory(LocalDateTime.now(), "SUCCESS", fileName));
            return fileName;

        } catch (Exception e) {
            historyRepo.save(new BackupHistory(LocalDateTime.now(), "FAILED", fileName));
            throw new RuntimeException("Không thể tạo file backup, vui lòng thử lại.");
        }
    }

    // scrum 179: tên file
    public String buildBackupFileName() {
        LocalDate today = LocalDate.now();
        return "file_backup_" + today.format(DateTimeFormatter.ofPattern("dd_MM_yyyy")) + ".txt";
    }

    // scrum 181: list file backup
    public List<String> listBackupFiles() {
        Path dir = Path.of(backupDir);
        if (!Files.exists(dir) || !Files.isDirectory(dir)) return List.of();

        try {
            return Files.list(dir)
                    .filter(Files::isRegularFile)
                    .sorted(Comparator.comparingLong((Path p) -> p.toFile().lastModified()).reversed())

                    .map(p -> p.getFileName().toString())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }

    // scrum 182: lịch sử backup
    public List<BackupHistory> getHistory() {
        return historyRepo.findAllByOrderByBackupTimeDesc();
    }
}
