package com.lms.lmsbackend.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseApprovalRequest {
    private Long courseId;
    private boolean approved;
}