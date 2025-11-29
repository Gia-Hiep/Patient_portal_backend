package com.patient_porta.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO trả về cho patient xem thông báo.
 */
@Data
public class NotificationDTO {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime postedAt;
    private String postedBy;
}
