package com.lms.lmsbackend.course.dto;

import com.lms.lmsbackend.course.model.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponse {

    private Long id;
    private String title;
    private String description;
    private String category;
    private Double price;

    private Long instructorId;
    private String instructorName;

    private CourseStatus status;

    private Integer totalLessons;
}