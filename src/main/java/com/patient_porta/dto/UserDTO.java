package com.patient_porta.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String role;
    private String status;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
