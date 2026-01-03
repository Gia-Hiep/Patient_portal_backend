package com.patient_porta.service;

import com.patient_porta.dto.DoctorAppointmentResponse;
import com.patient_porta.entity.Appointment;
import com.patient_porta.entity.PatientProfile;
import com.patient_porta.repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorAppointmentServiceTest {

    @Mock
    AppointmentRepository appointmentRepo;

    @InjectMocks
    DoctorAppointmentService service;

    private Appointment appt(String patientName, LocalDateTime at, Appointment.Status status) {
        Appointment a = new Appointment();
        a.setScheduledAt(at);
        a.setStatus(status);

        PatientProfile p = new PatientProfile();
        p.setFullName(patientName);
        a.setPatient(p);

        return a;
    }

    @BeforeEach
    void setup() {
        // nothing
    }

    @Test
    void getAppointmentsForDoctor_allStatuses_whenFilterBlank() {
        Long doctorUserId = 3L;
        String filter = "";
        String date = "2025-12-21";

        LocalDateTime t1 = LocalDateTime.of(2025, 12, 21, 8, 30);
        LocalDateTime t2 = LocalDateTime.of(2025, 12, 21, 10, 0);

        List<Appointment> fake = List.of(
                appt("A", t1, Appointment.Status.REQUESTED),
                appt("B", t2, Appointment.Status.COMPLETED)
        );

        // IMPORTANT:
        // Tên method mock phải khớp với AppointmentRepository hiện tại của bạn.
        // Nếu repo bạn đang là findByDoctor_UserId... thì đổi lại đúng tên.
        when(appointmentRepo.findByDoctor_UserIdAndScheduledAtBetweenOrderByScheduledAtAsc(
                eq(doctorUserId), any(LocalDateTime.class), any(LocalDateTime.class)
        )).thenReturn(fake);

        List<DoctorAppointmentResponse> res = service.getAppointmentsForDoctor(doctorUserId, filter, date);

        assertThat(res).hasSize(2);
        assertThat(res.get(0).getPatientName()).isEqualTo("A");
        assertThat(res.get(0).getStatus()).isEqualTo("REQUESTED");
        assertThat(res.get(1).getPatientName()).isEqualTo("B");
        assertThat(res.get(1).getStatus()).isEqualTo("COMPLETED");

        verify(appointmentRepo, times(1))
                .findByDoctor_UserIdAndScheduledAtBetweenOrderByScheduledAtAsc(eq(doctorUserId), any(), any());
        verify(appointmentRepo, never())
                .findByDoctor_UserIdAndScheduledAtBetweenAndStatusInOrderByScheduledAtAsc(any(), any(), any(), any());
    }

    @Test
    void getAppointmentsForDoctor_waitingFilter_mapsToRequestedConfirmed() {
        Long doctorUserId = 3L;
        String filter = "WAITING";
        String date = "2025-12-21";

        List<Appointment> fake = List.of(
                appt("Seed 01", LocalDateTime.of(2025, 12, 21, 8, 30), Appointment.Status.REQUESTED),
                appt("Seed 02", LocalDateTime.of(2025, 12, 21, 9, 0), Appointment.Status.CONFIRMED)
        );

        when(appointmentRepo.findByDoctor_UserIdAndScheduledAtBetweenAndStatusInOrderByScheduledAtAsc(
                eq(doctorUserId), any(LocalDateTime.class), any(LocalDateTime.class), anyList()
        )).thenReturn(fake);

        List<DoctorAppointmentResponse> res = service.getAppointmentsForDoctor(doctorUserId, filter, date);

        assertThat(res).hasSize(2);
        assertThat(res).allSatisfy(r ->
                assertThat(r.getStatus()).isIn("REQUESTED", "CONFIRMED")
        );

        ArgumentCaptor<List<Appointment.Status>> captor = ArgumentCaptor.forClass(List.class);
        verify(appointmentRepo).findByDoctor_UserIdAndScheduledAtBetweenAndStatusInOrderByScheduledAtAsc(
                eq(doctorUserId), any(), any(), captor.capture()
        );

        assertThat(captor.getValue()).containsExactlyInAnyOrder(
                Appointment.Status.REQUESTED,
                Appointment.Status.CONFIRMED
        );
    }

    @Test
    void getAppointmentsForDoctor_doneFilter_mapsToCompleted() {
        Long doctorUserId = 3L;

        when(appointmentRepo.findByDoctor_UserIdAndScheduledAtBetweenAndStatusInOrderByScheduledAtAsc(
                eq(doctorUserId), any(LocalDateTime.class), any(LocalDateTime.class), anyList()
        )).thenReturn(List.of(
                appt("Done", LocalDateTime.of(2025, 12, 21, 10, 0), Appointment.Status.COMPLETED)
        ));

        List<DoctorAppointmentResponse> res = service.getAppointmentsForDoctor(doctorUserId, "DONE", "2025-12-21");

        assertThat(res).hasSize(1);
        assertThat(res.get(0).getStatus()).isEqualTo("COMPLETED");
    }

    @Test
    void getAppointmentsForDoctor_cancelledFilter_mapsToCancelledNoShow() {
        Long doctorUserId = 3L;

        when(appointmentRepo.findByDoctor_UserIdAndScheduledAtBetweenAndStatusInOrderByScheduledAtAsc(
                eq(doctorUserId), any(LocalDateTime.class), any(LocalDateTime.class), anyList()
        )).thenReturn(List.of(
                appt("C1", LocalDateTime.of(2025, 12, 21, 11, 0), Appointment.Status.CANCELLED),
                appt("C2", LocalDateTime.of(2025, 12, 21, 11, 30), Appointment.Status.NO_SHOW)
        ));

        List<DoctorAppointmentResponse> res = service.getAppointmentsForDoctor(doctorUserId, "CANCELLED", "2025-12-21");

        assertThat(res).hasSize(2);
        assertThat(res).extracting(DoctorAppointmentResponse::getStatus)
                .containsExactlyInAnyOrder("CANCELLED", "NO_SHOW");
    }

    @Test
    void getAppointmentsForDoctor_unknownFilter_returnsEmpty_andDoesNotHitRepo() {
        List<DoctorAppointmentResponse> res =
                service.getAppointmentsForDoctor(3L, "SOMETHING_ELSE", "2025-12-21");

        assertThat(res).isEmpty();

        verifyNoInteractions(appointmentRepo);
    }
}
