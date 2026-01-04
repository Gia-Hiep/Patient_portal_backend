package com.patient_porta.service;

import com.patient_porta.dto.admin.MedicalServiceDTO;
import com.patient_porta.dto.MedicalServiceUpsertRequest;
import com.patient_porta.entity.MedicalService;
import com.patient_porta.repository.MedicalServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalServiceService {

    private final MedicalServiceRepository repo;

    // ==============================
    // PUBLIC: list dịch vụ active (patient/doctor)
    // ==============================
    public List<MedicalServiceDTO> listActive() {
        return repo.findByActiveTrueOrderByNameAsc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ==============================
    // ADMIN: list tất cả (kể cả inactive)
    // ==============================
    public List<MedicalServiceDTO> adminListAll() {
        return repo.findAll()
                .stream()
                .sorted((a, b) -> {
                    int c = Boolean.compare(b.isActive(), a.isActive()); // active trước
                    if (c != 0) return c;
                    String an = a.getName() == null ? "" : a.getName();
                    String bn = b.getName() == null ? "" : b.getName();
                    return an.compareToIgnoreCase(bn);
                })
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public MedicalServiceDTO adminCreate(MedicalServiceUpsertRequest req) {
        validate(req);

        MedicalService s = new MedicalService();
        s.setName(req.getName().trim());
        s.setDescription(safe(req.getDescription()));
        s.setPrice(req.getPrice());
        s.setActive(true);

        // code unique
        s.setCode(generateUniqueCode(req.getName()));

        return toDTO(repo.save(s));
    }

    public MedicalServiceDTO adminUpdate(Long id, MedicalServiceUpsertRequest req) {
        validate(req);

        MedicalService s = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy dịch vụ"));

        s.setName(req.getName().trim());
        s.setDescription(safe(req.getDescription()));
        s.setPrice(req.getPrice());

        return toDTO(repo.save(s));
    }

    /**
     * Soft delete để an toàn dữ liệu lịch sử (active=false)
     */
    public void adminDelete(Long id) {
        MedicalService s = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy dịch vụ"));
        s.setActive(false);
        repo.save(s);
    }

    // ==============================
    // Helpers
    // ==============================
    private void validate(MedicalServiceUpsertRequest req) {
        if (req == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ");
        }
        String name = req.getName();
        if (name == null || name.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên dịch vụ là bắt buộc");
        }
        BigDecimal price = req.getPrice();
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            // ✅ đúng yêu cầu đề bài
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giá không hợp lệ.");
        }
    }

    private String safe(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private MedicalServiceDTO toDTO(MedicalService s) {
        MedicalServiceDTO dto = new MedicalServiceDTO();
        dto.setId(s.getId());
        dto.setCode(s.getCode());
        dto.setName(s.getName());
        dto.setDescription(s.getDescription());
        dto.setPrice(s.getPrice());
        dto.setActive(s.isActive());
        return dto;
    }

    private String generateUniqueCode(String name) {
        String base = slugToCode(name);
        for (int i = 0; i < 6; i++) {
            String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase(Locale.ROOT);
            String code = (base + "_" + suffix);
            if (code.length() > 30) code = code.substring(0, 30);
            if (!repo.existsByCode(code)) return code;
        }
        String code = "SRV_" + System.currentTimeMillis();
        if (code.length() > 30) code = code.substring(0, 30);
        return code;
    }

    private String slugToCode(String s) {
        String normalized = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        String up = normalized.toUpperCase(Locale.ROOT);
        String cleaned = up.replaceAll("[^A-Z0-9]+", "_").replaceAll("^_+|_+$", "");
        if (cleaned.isBlank()) cleaned = "SERVICE";
        if (cleaned.length() > 18) cleaned = cleaned.substring(0, 18);
        return cleaned;
    }
}
