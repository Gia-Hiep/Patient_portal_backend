package com.patient_porta.service;

import com.patient_porta.dto.ChatMessageDTO;
import com.patient_porta.dto.ChatPeerDTO;
import com.patient_porta.dto.SendMessageRequest;
import com.patient_porta.entity.Message;
import com.patient_porta.entity.User;
import com.patient_porta.repository.AppointmentRepository;
import com.patient_porta.repository.DoctorProfileRepository;
import com.patient_porta.repository.MessageRepository;
import com.patient_porta.repository.PatientProfileRepository;
import com.patient_porta.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository msgRepo;
    private final UserRepository userRepo;
    private final DoctorProfileRepository doctorProfileRepo;
    private final PatientProfileRepository patientProfileRepo;
    private final AppointmentRepository appointmentRepo;

    /* ================= helpers ================= */

    private User me() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private void requireRole(User u, User.Role role) {
        if (u.getRole() != role)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền truy cập");
    }

    private String textOnly(String s) {
        if (s == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nội dung không hợp lệ");
        String v = s.trim();
        if (v.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng nhập tin nhắn");
        if (v.length() > 2000) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tin nhắn quá dài");
        return v;
    }

    private ChatMessageDTO toDto(Message m) {
        ChatMessageDTO d = new ChatMessageDTO();
        d.setId(m.getId());
        d.setPatientId(m.getPatientId());
        d.setDoctorId(m.getDoctorId());
        d.setSenderUserId(m.getSenderUserId());
        d.setContent(m.getContent());
        d.setSentAt(m.getSentAt());
        d.setSenderRole(Objects.equals(m.getSenderUserId(), m.getPatientId()) ? "PATIENT" : "DOCTOR");
        return d;
    }

    private Message buildMessage(Long patientId, Long doctorId, Long senderId, String content) {
        Message m = new Message();
        m.setPatientId(patientId);
        m.setDoctorId(doctorId);
        m.setSenderUserId(senderId);
        m.setContent(textOnly(content));
        m.setSentAt(Instant.now());
        return m;
    }

    /* ================= PATIENT ================= */

    // list bác sĩ đã từng khám (để chọn chat)
    public List<ChatPeerDTO> myDoctors() {
        User u = me();
        requireRole(u, User.Role.PATIENT);

        List<Long> ids = appointmentRepo.findDistinctDoctorIdsByPatientId(u.getId());
        return ids.stream()
                .map(id -> doctorProfileRepo.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .map(dp -> new ChatPeerDTO(dp.getUserId(), dp.getFullName()))
                .toList();
    }

    // xem lịch sử chat với 1 bác sĩ (chỉ thread của mình)
    public List<ChatMessageDTO> patientThread(Long doctorId) {
        User u = me();
        requireRole(u, User.Role.PATIENT);

        return msgRepo.findByPatientIdAndDoctorIdOrderBySentAtAsc(u.getId(), doctorId)
                .stream().map(this::toDto).toList();
    }

    // gửi tin nhắn cho bác sĩ
    @Transactional
    public ChatMessageDTO patientSend(Long doctorId, SendMessageRequest req) {
        User u = me();
        requireRole(u, User.Role.PATIENT);

        doctorProfileRepo.findById(doctorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bác sĩ không tồn tại"));

        Message saved = msgRepo.save(buildMessage(u.getId(), doctorId, u.getId(), req.getContent()));
        return toDto(saved);
    }

    /* ================= DOCTOR ================= */

    // list bệnh nhân đã chat với bác sĩ hiện tại (+ search)
    public List<ChatPeerDTO> doctorPatients(String q) {
        User u = me();
        requireRole(u, User.Role.DOCTOR);

        String key = (q == null) ? "" : q.trim().toLowerCase();

        return msgRepo.findPatientIdsChattedWithDoctor(u.getId()).stream()
                .map(pid -> patientProfileRepo.findById(pid).orElse(null))
                .filter(Objects::nonNull)
                .map(pp -> new ChatPeerDTO(pp.getUserId(), pp.getFullName()))
                .filter(p -> key.isEmpty() || (p.getFullName() != null && p.getFullName().toLowerCase().contains(key)))
                .toList();
    }

    // xem thread với 1 bệnh nhân (chỉ thread của mình)
    public List<ChatMessageDTO> doctorThread(Long patientId) {
        User u = me();
        requireRole(u, User.Role.DOCTOR);

        return msgRepo.findByPatientIdAndDoctorIdOrderBySentAtAsc(patientId, u.getId())
                .stream().map(this::toDto).toList();
    }

    // bác sĩ gửi tin nhắn cho bệnh nhân
    @Transactional
    public ChatMessageDTO doctorSend(Long patientId, SendMessageRequest req) {
        User u = me();
        requireRole(u, User.Role.DOCTOR);

        patientProfileRepo.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bệnh nhân không tồn tại"));

        Message saved = msgRepo.save(buildMessage(patientId, u.getId(), u.getId(), req.getContent()));
        return toDto(saved);
    }
}
