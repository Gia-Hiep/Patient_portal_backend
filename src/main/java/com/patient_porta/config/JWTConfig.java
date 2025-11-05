package com.patient_porta.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.patient_porta.service.JwtService;

public class JWTConfig {

    @Configuration
    public class JwtConfig {

        @Value("${jwt.secret}")
        private String jwtSecret;

        @Value("${jwt.expiration}")
        private long jwtExpiration;

        @Bean
        public JwtService jwtService() {
            return new JwtService(jwtSecret, jwtExpiration);
        }
    }
}
