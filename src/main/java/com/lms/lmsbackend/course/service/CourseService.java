package com.lms.lmsbackend.course.service;

import com.lms.lmsbackend.course.dto.CourseRequest;
import com.lms.lmsbackend.course.dto.CourseResponse;

import java.util.List;

public interface CourseService {

    CourseResponse addCourse(CourseRequest request, Long instructorId);
    CourseResponse updateCourse(Long id, CourseRequest request);
    void deleteCourse(Long id);

    List<CourseResponse> getCoursesByInstructor(Long instructorId);
    List<CourseResponse> getAllCourses();
    List<CourseResponse> getApprovedCourses();

    CourseResponse getCourseById(Long id);
}