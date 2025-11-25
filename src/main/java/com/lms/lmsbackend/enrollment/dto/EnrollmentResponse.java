package com.lms.lmsbackend.enrollment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {
    private Long id;
    private Long studentId;
    private Long courseId;
    private LocalDateTime enrolledAt;
}