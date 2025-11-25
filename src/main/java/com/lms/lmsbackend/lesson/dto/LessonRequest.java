package com.lms.lmsbackend.lesson.dto;

import lombok.Data;

@Data
public class LessonRequest {
    private String title;
    private String content;
    private String fileUrl;
    private Long courseId;
}