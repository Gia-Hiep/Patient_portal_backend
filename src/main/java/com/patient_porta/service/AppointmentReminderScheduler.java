package com.patient_porta.service;

import com.patient_porta.entity.Appointment;
import com.patient_porta.entity.UserNotification;
import com.patient_porta.entity.UserNotification.RelatedType;
import com.patient_porta.entity.UserNotification.Type;
import com.patient_porta.repository.AppointmentRepository;
import com.patient_porta.repository.UserNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentReminderScheduler {

    private final AppointmentRepository appointmentRepository;
    private final NotificationEventService notificationEventService;
    private final UserNotificationRepository notificationRepository;

    @Scheduled(fixedDelay = 60_000)
    public void sendUpcomingReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.plusMinutes(29);
        LocalDateTime to = now.plusMinutes(31);

        List<Appointment> upcoming = appointmentRepository.findAppointmentsBetween(from, to);
        for (Appointment appt : upcoming) {
            // tránh gửi trùng: nếu đã có notification loại APPT_REMINDER cho appointment đó thì bỏ qua
            boolean exists = notificationRepository
                    .existsByRelatedTypeAndRelatedIdAndType(
                            RelatedType.APPOINTMENT,
                            appt.getId(),
                            Type.APPT_REMINDER
                    );
            if (exists) continue;

            String dept = "khám bệnh";
            notificationEventService.notifyUpcomingAppointment(
                    appt.getPatient().getUserId(),
                    appt.getId(),
                    appt.getScheduledAt(),
                    dept
            );
            log.info("Created appointment reminder for appointment {}", appt.getId());
        }
    }
}
