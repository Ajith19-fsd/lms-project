package com.lms.lmsbackend.student.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {
    private Long courseId;
    private String courseTitle;
    private String instructorName;
    private String enrollmentStatus;  // e.g., "Enrolled"
}
