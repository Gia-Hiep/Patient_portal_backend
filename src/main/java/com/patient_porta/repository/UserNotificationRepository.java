package com.patient_porta.repository;

import com.patient_porta.entity.UserNotification;
import com.patient_porta.entity.UserNotification.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {

    // Lấy tất cả thông báo của 1 user, mới nhất lên trên
    List<UserNotification> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Tìm 1 thông báo nhưng phải thuộc về user đó
    Optional<UserNotification> findByIdAndUserId(Long id, Long userId);

    long countByUserIdAndStatus(Long userId, Status status);
    boolean existsByRelatedTypeAndRelatedIdAndType(
            UserNotification.RelatedType relatedType,
            Long relatedId,
            UserNotification.Type type
    );

}

