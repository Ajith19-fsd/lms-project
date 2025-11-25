package com.lms.lmsbackend.frontend.controller;

import com.lms.lmsbackend.course.model.Course;
import com.lms.lmsbackend.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/student")
public class StudentCourseController {

    private final StudentService studentService;

    // VIEW COURSE DETAILS
    @GetMapping("/course/{courseId}")
    public String viewCourse(@PathVariable Long courseId, Model model) {
        Course course = studentService.getCourseById(courseId);
        model.addAttribute("course", course);
        return "student/course-view-student";
    }

    // VIEW ALL LESSONS FOR COURSE
    @GetMapping("/course/{courseId}/lessons")
    public String viewLessons(@PathVariable Long courseId, Model model) {
        Course course = studentService.getCourseById(courseId);
        model.addAttribute("courseId", courseId);
        model.addAttribute("courseTitle", course.getTitle());
        return "student/course_lessons";
    }

    // VIEW SINGLE LESSON
    @GetMapping("/course/{courseId}/lesson/{lessonId}")
    public String viewLesson(@PathVariable Long courseId,
                             @PathVariable Long lessonId,
                             Model model) {

        // Pass lessonId AND courseId to Thymeleaf
        model.addAttribute("lessonId", lessonId);
        model.addAttribute("courseId", courseId);

        return "student/lesson-view-student";
    }
}