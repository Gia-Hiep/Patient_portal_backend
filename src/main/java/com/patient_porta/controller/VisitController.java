package com.patient_porta.controller;

import com.patient_porta.dto.VisitDetailDTO;
import com.patient_porta.dto.VisitSummaryDTO;
import com.patient_porta.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
public class VisitController {

    private final VisitService visitService;

    // GET /api/visits  -> list lịch sử khám của bệnh nhân hiện tại
    @GetMapping
    public ResponseEntity<List<VisitSummaryDTO>> getMyVisits() {
        return ResponseEntity.ok(visitService.getMyVisits());
    }

    // GET /api/visits/{id} -> chi tiết 1 lần khám (đã check đúng bệnh nhân trong service)
    @GetMapping("/{id}")
    public ResponseEntity<VisitDetailDTO> getVisitDetail(@PathVariable Long id) {
        return ResponseEntity.ok(visitService.getMyVisitDetail(id));
    }
}
