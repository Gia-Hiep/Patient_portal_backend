package com.patient_porta.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationEntryPoint restAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            Map<String, Object> body = new HashMap<>();
            body.put("code", "UNAUTHORIZED");
            body.put("message", authException.getMessage());
            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ======================
                // STATELESS + CORS
                // ======================
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())

                // ======================
                // EXCEPTION
                // ======================
                .exceptionHandling(ex -> ex.authenticationEntryPoint(restAuthenticationEntryPoint()))

                // ======================
                // AUTHORIZATION
                // ======================
                .authorizeHttpRequests(auth -> auth

                        // Preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Public
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/error").permitAll()

                        // 🔔 USER SETTINGS
                        .requestMatchers("/api/settings/**").authenticated()

                        // 👤 PROFILE
                        .requestMatchers("/api/profile/**").authenticated()

                        // 🧍 PATIENT – xem tiến trình
                        .requestMatchers(HttpMethod.GET, "/api/process/**")
                        .hasAnyRole("PATIENT", "DOCTOR")

                        // 👨‍⚕️ DOCTOR – cập nhật tiến trình
                        .requestMatchers("/api/examination-progress/**")
                        .hasRole("DOCTOR")

                        // 🔔 NOTIFICATION
                        .requestMatchers("/api/autonotification/**").authenticated()

                        // FALLBACK
                        .anyRequest().authenticated()
                )

                // ======================
                // JWT FILTER
                // ======================
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();

        // Cho phép tất cả origin dạng http://localhost:xxxx
        cfg.setAllowedOriginPatterns(Arrays.asList("http://localhost:*"));

        cfg.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        cfg.setAllowedHeaders(Arrays.asList("*"));
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
