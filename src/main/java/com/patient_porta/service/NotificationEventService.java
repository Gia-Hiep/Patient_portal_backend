package com.patient_porta.service;

import com.patient_porta.entity.User;
import com.patient_porta.entity.UserNotification;
import com.patient_porta.entity.UserNotification.RelatedType;
import com.patient_porta.entity.UserNotification.Status;
import com.patient_porta.entity.UserNotification.Type;
import com.patient_porta.repository.UserNotificationRepository;
import com.patient_porta.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationEventService {

    private final UserNotificationRepository notificationRepository;
    private final UserRepository userRepository;

    private boolean isAutoNotifyEnabled(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // nếu chưa thêm cột autoNotifyEnabled thì tạm thời luôn trả true
        Boolean enabled = user.getAutoNotifyEnabled();
        return enabled == null || enabled;
    }

    private UserNotification base(Long userId) {
        UserNotification n = new UserNotification();
        n.setUserId(userId);
        n.setStatus(Status.UNREAD);
        n.setCreatedAt(LocalDateTime.now());
        n.setRelatedType(RelatedType.NONE);
        return n;
    }

    /** Khi có kết quả xét nghiệm mới */
    public void notifyLabResultReady(Long userId, Long documentId, String testName) {
        if (!isAutoNotifyEnabled(userId)) return;

        UserNotification n = base(userId);
        n.setType(Type.LAB_READY);
        n.setTitle("Kết quả xét nghiệm");
        n.setBody("Kết quả " + testName + " đã có, xem ngay.");
        n.setRelatedType(RelatedType.DOCUMENT);
        n.setRelatedId(documentId);
        notificationRepository.save(n);
    }

    /** Nhắc sắp đến lượt khám (trước giờ h 1 khoảng thời gian) */
    public void notifyUpcomingAppointment(Long userId, Long appointmentId,
                                          LocalDateTime scheduledAt, String departmentName) {
        if (!isAutoNotifyEnabled(userId)) return;

        UserNotification n = base(userId);
        n.setType(Type.APPT_REMINDER);
        n.setTitle("Đến lượt khám");
        n.setBody("Bạn sắp được gọi vào phòng " + departmentName + ".");
        n.setRelatedType(RelatedType.APPOINTMENT);
        n.setRelatedId(appointmentId);
        notificationRepository.save(n);
    }

    /** Nhắc tái khám */
    public void notifyRevisitReminder(Long userId, Long appointmentId,
                                      LocalDateTime revisitDate) {
        if (!isAutoNotifyEnabled(userId)) return;

        UserNotification n = base(userId);
        n.setType(Type.REVISIT_REMINDER);
        n.setTitle("Nhắc tái khám");
        n.setBody("Bạn có lịch tái khám vào ngày " + revisitDate.toLocalDate() + ".");
        n.setRelatedType(RelatedType.APPOINTMENT);
        n.setRelatedId(appointmentId);
        notificationRepository.save(n);
    }

    /** Thông báo hệ thống chung cho riêng 1 user */
    public void notifySystemMessage(Long userId, String message) {
        if (!isAutoNotifyEnabled(userId)) return;

        UserNotification n = base(userId);
        n.setType(Type.SYSTEM);
        n.setTitle("Thông báo hệ thống");
        n.setBody(message);
        n.setRelatedType(RelatedType.NONE);
        notificationRepository.save(n);
    }
}
