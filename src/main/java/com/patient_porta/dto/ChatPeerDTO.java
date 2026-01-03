package com.patient_porta.dto;

import lombok.Data;
import lombok.Getter; import lombok.Setter;

@Data
public class ChatPeerDTO {
    private Long id;        // user_id của doctor/patient
    private String fullName;

    public ChatPeerDTO(Long userId, String fullName) {
        this.id = userId;
        this.fullName = fullName;
    }
}
