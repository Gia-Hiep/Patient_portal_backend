package com.patient_porta.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
    public void send(String to, String subject, String content) {
        System.out.println("=== SEND EMAIL ===");
        System.out.println("To: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Body:\n" + content);
    }
}
