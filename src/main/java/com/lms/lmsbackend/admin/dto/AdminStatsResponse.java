package com.lms.lmsbackend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminStatsResponse {
    private long totalUsers;
    private long totalInstructors;
    private long totalStudents;
    private long totalCourses;
}