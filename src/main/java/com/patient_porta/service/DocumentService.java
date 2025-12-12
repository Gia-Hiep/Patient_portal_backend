package com.patient_porta.service;

import com.patient_porta.entity.Document;
import com.patient_porta.entity.User;
import com.patient_porta.repository.DocumentRepository;
import com.patient_porta.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;

    @Value("${files.storage-root:./}") // cấu hình trong application.yml
    private String storageRoot;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "User not found"));
    }

    private Path resolveSafe(String relative) {
        if (relative == null || relative.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "File path trống");
        }

        // Chuẩn hoá slash và bỏ leading '/'
        String rel = relative.replace("\\", "/");
        if (rel.startsWith("/")) rel = rel.substring(1); // "/uploads/..." -> "uploads/..."

        if (rel.startsWith("files/")) {
            rel = "uploads/" + rel.substring("files/".length());
        } else if (!rel.startsWith("uploads/")) {
            rel = "uploads/" + rel;
        }

        Path base = Paths.get(storageRoot).toAbsolutePath().normalize();       // "./"
        Path uploadsBase = base.resolve("uploads").normalize();                // "./uploads"
        Path resolved = base.resolve(rel).normalize();                         // "./uploads/..."

        if (!resolved.startsWith(uploadsBase)) {
            throw new ResponseStatusException(FORBIDDEN, "Invalid file path");
        }
        return resolved;
    }

    private Document loadOwnedDoc(Long docId, Long patientUserId) {
        return documentRepository.findByIdAndPatient_UserId(docId, patientUserId)
                .orElseThrow(() -> new ResponseStatusException(FORBIDDEN, "Truy cập bị từ chối."));
    }


    public ResponseEntity<Resource> streamPdf(Long docId, boolean download) throws IOException {
        User me = getCurrentUser();
        if (me.getRole() != User.Role.PATIENT) {
            throw new ResponseStatusException(FORBIDDEN, "Chỉ bệnh nhân mới xem/tải tài liệu của mình");
        }

        Document d = loadOwnedDoc(docId, me.getId());

        Path file = resolveSafe(d.getFilePath());
        if (!Files.exists(file)) {
            // Theo AC: báo "Kết quả chưa sẵn sàng..."
            // Trả 404 là hợp lý cho client (hoặc 200 + payload code NOT_READY nếu FE cần).
            throw new ResponseStatusException(NOT_FOUND, "Kết quả chưa sẵn sàng, vui lòng quay lại sau.");
        }

        String filename = (d.getTitle() == null ? ("document-" + d.getId()) : d.getTitle()) + ".pdf";
        String mime = (d.getMimeType() == null || d.getMimeType().isBlank())
                ? MediaType.APPLICATION_PDF_VALUE
                : d.getMimeType();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, mime);
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                (download ? "attachment; filename=\"" : "inline; filename=\"") + filename + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(Files.newInputStream(file)));
    }
}
