package com.lms.lmsbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * Global CORS configuration for LMS project.
 * Ensures external clients (React, Android, etc.) can access backend APIs securely.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 🌍 Allowed Origins (local dev + production)
        config.setAllowedOrigins(List.of(
                "http://localhost:8080",           // Thymeleaf UI
                "http://localhost:5173",           // React / Vite dev
                "https://your-frontend-domain.com" // Production
        ));

        // Allowed HTTP Methods
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // Allowed Headers
        config.setAllowedHeaders(List.of(
                AppConstants.HEADER_STRING,
                "Content-Type",
                "Cache-Control"
        ));

        // Allow credentials
        config.setAllowCredentials(true);

        // Exposed Headers (for JWT access on frontend)
        config.setExposedHeaders(List.of(AppConstants.HEADER_STRING));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}