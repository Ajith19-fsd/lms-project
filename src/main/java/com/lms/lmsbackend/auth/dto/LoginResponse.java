package com.lms.lmsbackend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;    // JWT token
    private String role;     // ADMIN / INSTRUCTOR / STUDENT
    private Long userId;     // User ID
    private String message;  // Optional message for frontend
}
