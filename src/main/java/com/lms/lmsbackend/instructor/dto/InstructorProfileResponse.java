package com.lms.lmsbackend.instructor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InstructorProfileResponse {
    private String fullName;
    private String username;
    private String email;
    private String role; // e.g., "INSTRUCTOR"
}
