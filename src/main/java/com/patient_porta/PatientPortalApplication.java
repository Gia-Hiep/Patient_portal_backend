package com.patient_porta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PatientPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(PatientPortalApplication.class, args);
    }
}
