package com.patient_porta.service;

import com.patient_porta.dto.VisitDetailDTO;
import com.patient_porta.dto.VisitDocumentDTO;
import com.patient_porta.dto.VisitSummaryDTO;
import com.patient_porta.entity.Appointment;
import com.patient_porta.entity.Document;
import com.patient_porta.entity.Document.DocType;
import com.patient_porta.entity.User;
import com.patient_porta.repository.AppointmentRepository;
import com.patient_porta.repository.DocumentRepository;
import com.patient_porta.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisitService {

    private final AppointmentRepository appointmentRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User not found"
                ));
    }

    public List<VisitSummaryDTO> getMyVisits() {
        User user = getCurrentUser();
        if (user.getRole() != User.Role.PATIENT) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Chỉ bệnh nhân mới xem được lịch sử khám của mình"
            );
        }

        List<Appointment> visits =
                appointmentRepository.findByPatient_User_IdOrderByScheduledAtDesc(user.getId());

        return visits.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    public VisitDetailDTO getMyVisitDetail(Long visitId) {
        User user = getCurrentUser();
        if (user.getRole() != User.Role.PATIENT) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Chỉ bệnh nhân mới xem được lịch sử khám của mình"
            );
        }

        Appointment appt = appointmentRepository
                .findByIdAndPatient_User_Id(visitId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Truy cập bị từ chối."
                ));

        List<Document> docs = documentRepository.findByAppointment_IdAndDocTypeIn(
                appt.getId(),
                Arrays.asList(DocType.LAB, DocType.IMAGING)
        );

        return toDetailDTO(appt, docs);
    }

    // ====== helper nhỏ tránh null/chuỗi rỗng ======
    private String safe(String s, String fallback) {
        return (s == null || s.isBlank()) ? fallback : s;
    }

    private VisitSummaryDTO toSummaryDTO(Appointment appt) {
        VisitSummaryDTO dto = new VisitSummaryDTO();
        dto.setId(appt.getId());
        dto.setVisitDate(appt.getScheduledAt());

        // ===== Khoa khám =====
        String dept = null;
        if (appt.getDoctor() != null && appt.getDoctor().getDepartment() != null) {
            dept = appt.getDoctor().getDepartment();
        } else if (appt.getService() != null) {
            dept = appt.getService().getName();
        }
        dto.setDepartment(safe(dept, "Chưa cập nhật khoa"));

        // ===== Bác sĩ =====
        String doctorName = (appt.getDoctor() != null)
                ? appt.getDoctor().getFullName()
                : null;
        dto.setDoctorName(safe(doctorName, "Chưa có bác sĩ"));

        // ===== Chẩn đoán tóm tắt =====
        dto.setDiagnosisShort(safe(appt.getNotes(), "Chưa cập nhật chẩn đoán"));

        dto.setStatus(appt.getStatus() != null ? appt.getStatus().name() : null);
        return dto;
    }

    private VisitDetailDTO toDetailDTO(Appointment appt, List<Document> docs) {
        VisitDetailDTO dto = new VisitDetailDTO();
        dto.setId(appt.getId());
        dto.setVisitDate(appt.getScheduledAt());

        String dept = null;
        if (appt.getDoctor() != null && appt.getDoctor().getDepartment() != null) {
            dept = appt.getDoctor().getDepartment();
        } else if (appt.getService() != null) {
            dept = appt.getService().getName();
        }
        dto.setDepartment(safe(dept, "Chưa cập nhật khoa"));

        String doctorName = (appt.getDoctor() != null)
                ? appt.getDoctor().getFullName()
                : null;
        dto.setDoctorName(safe(doctorName, "Chưa có bác sĩ"));

        if (appt.getService() != null) {
            dto.setServiceName(appt.getService().getName());
        }

        String diag = safe(appt.getNotes(), "Chưa cập nhật chẩn đoán");
        dto.setDiagnosisShort(diag);
        dto.setDiagnosisDetail(diag);

        dto.setPrescription("Đơn thuốc: " + diag);
        dto.setTreatmentHistory("Lịch sử điều trị: " + diag);

        dto.setStatus(appt.getStatus() != null ? appt.getStatus().name() : null);

        dto.setDocuments(
                docs.stream().map(this::toDocDTO).collect(Collectors.toList())
        );

        return dto;
    }

    private VisitDocumentDTO toDocDTO(Document d) {
        VisitDocumentDTO dto = new VisitDocumentDTO();
        dto.setId(d.getId());
        dto.setType(d.getDocType() != null ? d.getDocType().name() : null);
        dto.setTitle(d.getTitle());
        dto.setMimeType(d.getMimeType());

        String base = "/api/documents/" + d.getId();
        dto.setViewUrl(base + "/view");
        dto.setDownloadUrl(base + "/download");

        return dto;
    }
}
