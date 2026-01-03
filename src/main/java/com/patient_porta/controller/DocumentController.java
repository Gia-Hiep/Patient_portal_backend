package com.patient_porta.controller;

import com.patient_porta.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    // Xem trong trình duyệt (inline)
    @GetMapping("/{id}/view")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Resource> view(@PathVariable Long id) throws IOException {
        return documentService.streamPdf(id, false);
    }

    // Tải về (attachment)
    @GetMapping("/{id}/download")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws IOException {
        return documentService.streamPdf(id, true);
    }
}
