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
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock MessageRepository msgRepo;
    @Mock UserRepository userRepo;
    @Mock DoctorProfileRepository doctorProfileRepo;
    @Mock PatientProfileRepository patientProfileRepo;
    @Mock AppointmentRepository appointmentRepo;

    @InjectMocks ChatService chatService;

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    private void authAs(String username) {
        var auth = new UsernamePasswordAuthenticationToken(username, "pw", List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private User user(Long id, String username, User.Role role) {
        User u = new User();
        u.setId(id);
        u.setUsername(username);
        u.setRole(role);
        return u;
    }

    /* ================= me() / role ================= */

    @Test
    void myDoctors_unauthorized_when_user_not_found() {
        authAs("nope");
        when(userRepo.findByUsername("nope")).thenReturn(Optional.empty());

        var ex = assertThrows(ResponseStatusException.class, () -> chatService.myDoctors());
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void myDoctors_forbidden_when_not_patient() {
        authAs("doc1");
        when(userRepo.findByUsername("doc1")).thenReturn(Optional.of(user(10L, "doc1", User.Role.DOCTOR)));

        var ex = assertThrows(ResponseStatusException.class, () -> chatService.myDoctors());
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    /* ================= PATIENT ================= */

    @Test
    void myDoctors_returns_distinct_doctors_from_appointments() {
        authAs("pat1");
        User me = user(1L, "pat1", User.Role.PATIENT);
        when(userRepo.findByUsername("pat1")).thenReturn(Optional.of(me));

        when(appointmentRepo.findDistinctDoctorIdsByPatientId(1L)).thenReturn(List.of(101L, 102L));

        // giả lập DoctorProfile entity có getUserId(), getFullName()
        var dp1 = mock(com.patient_porta.entity.DoctorProfile.class);
        when(dp1.getUserId()).thenReturn(101L);
        when(dp1.getFullName()).thenReturn("Dr A");

        var dp2 = mock(com.patient_porta.entity.DoctorProfile.class);
        when(dp2.getUserId()).thenReturn(102L);
        when(dp2.getFullName()).thenReturn("Dr B");

        when(doctorProfileRepo.findById(101L)).thenReturn(Optional.of(dp1));
        when(doctorProfileRepo.findById(102L)).thenReturn(Optional.of(dp2));

        List<ChatPeerDTO> out = chatService.myDoctors();

        assertEquals(2, out.size());
        assertEquals(101L, out.get(0).getId());
        assertEquals("Dr A", out.get(0).getFullName());
        assertEquals(102L, out.get(1).getId());
        assertEquals("Dr B", out.get(1).getFullName());
    }

    @Test
    void patientThread_calls_repo_with_me_patientId() {
        authAs("pat1");
        User me = user(1L, "pat1", User.Role.PATIENT);
        when(userRepo.findByUsername("pat1")).thenReturn(Optional.of(me));

        Message m = new Message();
        m.setId(9L);
        m.setPatientId(1L);
        m.setDoctorId(101L);
        m.setSenderUserId(1L);
        m.setContent("hi");
        m.setSentAt(Instant.now());

        when(msgRepo.findByPatientIdAndDoctorIdOrderBySentAtAsc(1L, 101L)).thenReturn(List.of(m));

        List<ChatMessageDTO> out = chatService.patientThread(101L);

        assertEquals(1, out.size());
        assertEquals(9L, out.get(0).getId());
        assertEquals("PATIENT", out.get(0).getSenderRole());
        verify(msgRepo).findByPatientIdAndDoctorIdOrderBySentAtAsc(1L, 101L);
    }

    @Test
    void patientSend_bad_request_when_doctor_not_exists() {
        authAs("pat1");
        User me = user(1L, "pat1", User.Role.PATIENT);
        when(userRepo.findByUsername("pat1")).thenReturn(Optional.of(me));

        when(doctorProfileRepo.findById(101L)).thenReturn(Optional.empty());

        SendMessageRequest req = new SendMessageRequest();
        req.setContent("hello");

        var ex = assertThrows(ResponseStatusException.class, () -> chatService.patientSend(101L, req));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void patientSend_saves_message_and_returns_dto() {
        authAs("pat1");
        User me = user(1L, "pat1", User.Role.PATIENT);
        when(userRepo.findByUsername("pat1")).thenReturn(Optional.of(me));

        var dp = mock(com.patient_porta.entity.DoctorProfile.class);
        when(doctorProfileRepo.findById(101L)).thenReturn(Optional.of(dp));

        SendMessageRequest req = new SendMessageRequest();
        req.setContent("  hello  ");

        when(msgRepo.save(any(Message.class))).thenAnswer(inv -> {
            Message m = inv.getArgument(0, Message.class);
            m.setId(999L);
            return m;
        });

        ChatMessageDTO out = chatService.patientSend(101L, req);

        assertEquals(999L, out.getId());
        assertEquals(1L, out.getPatientId());
        assertEquals(101L, out.getDoctorId());
        assertEquals(1L, out.getSenderUserId());
        assertEquals("hello", out.getContent());
        assertEquals("PATIENT", out.getSenderRole());

        ArgumentCaptor<Message> cap = ArgumentCaptor.forClass(Message.class);
        verify(msgRepo).save(cap.capture());
        assertEquals("hello", cap.getValue().getContent());
        assertNotNull(cap.getValue().getSentAt());
    }

    @Test
    void patientSend_validation_fails_when_content_blank() {
        authAs("pat1");
        User me = user(1L, "pat1", User.Role.PATIENT);
        when(userRepo.findByUsername("pat1")).thenReturn(Optional.of(me));

        var dp = mock(com.patient_porta.entity.DoctorProfile.class);
        when(doctorProfileRepo.findById(101L)).thenReturn(Optional.of(dp));

        SendMessageRequest req = new SendMessageRequest();
        req.setContent("   ");

        var ex = assertThrows(ResponseStatusException.class, () -> chatService.patientSend(101L, req));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(msgRepo, never()).save(any());
    }

    /* ================= DOCTOR ================= */

    @Test
    void doctorPatients_filters_by_q_case_insensitive() {
        authAs("doc1");
        User me = user(2L, "doc1", User.Role.DOCTOR);
        when(userRepo.findByUsername("doc1")).thenReturn(Optional.of(me));

        when(msgRepo.findPatientIdsChattedWithDoctor(2L)).thenReturn(List.of(11L, 12L));

        var p1 = mock(com.patient_porta.entity.PatientProfile.class);
        when(p1.getUserId()).thenReturn(11L);
        when(p1.getFullName()).thenReturn("Nguyen Van A");

        var p2 = mock(com.patient_porta.entity.PatientProfile.class);
        when(p2.getUserId()).thenReturn(12L);
        when(p2.getFullName()).thenReturn("Tran Thi B");

        when(patientProfileRepo.findById(11L)).thenReturn(Optional.of(p1));
        when(patientProfileRepo.findById(12L)).thenReturn(Optional.of(p2));

        List<ChatPeerDTO> out = chatService.doctorPatients("van");

        assertEquals(1, out.size());
        assertEquals(11L, out.get(0).getId());
        assertEquals("Nguyen Van A", out.get(0).getFullName());
    }

    @Test
    void doctorThread_calls_repo_with_patientId_and_my_doctorId() {
        authAs("doc1");
        User me = user(2L, "doc1", User.Role.DOCTOR);
        when(userRepo.findByUsername("doc1")).thenReturn(Optional.of(me));

        Message m = new Message();
        m.setId(5L);
        m.setPatientId(11L);
        m.setDoctorId(2L);
        m.setSenderUserId(2L);
        m.setContent("ok");
        m.setSentAt(Instant.now());

        when(msgRepo.findByPatientIdAndDoctorIdOrderBySentAtAsc(11L, 2L)).thenReturn(List.of(m));

        List<ChatMessageDTO> out = chatService.doctorThread(11L);

        assertEquals(1, out.size());
        assertEquals("DOCTOR", out.get(0).getSenderRole());
        verify(msgRepo).findByPatientIdAndDoctorIdOrderBySentAtAsc(11L, 2L);
    }

    @Test
    void doctorSend_bad_request_when_patient_not_exists() {
        authAs("doc1");
        User me = user(2L, "doc1", User.Role.DOCTOR);
        when(userRepo.findByUsername("doc1")).thenReturn(Optional.of(me));

        when(patientProfileRepo.findById(11L)).thenReturn(Optional.empty());

        SendMessageRequest req = new SendMessageRequest();
        req.setContent("hello");

        var ex = assertThrows(ResponseStatusException.class, () -> chatService.doctorSend(11L, req));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }
}
