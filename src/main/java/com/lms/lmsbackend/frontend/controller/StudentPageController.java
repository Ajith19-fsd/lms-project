package com.lms.lmsbackend.frontend.controller;

import com.lms.lmsbackend.course.dto.CourseResponse;
import com.lms.lmsbackend.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/student")
public class StudentPageController {

    private final CourseService courseService;

    // -------------------------
    // Dashboard
    // -------------------------
    @GetMapping("/dashboard")
    public String dashboardPage(Model model) {
        return "student/dashboard_student";
    }

    // -------------------------
    // View Courses Page
    // -------------------------
    @GetMapping("/view-courses")
    public String viewCoursesPage() {
        return "student/view-courses";
    }

    // -------------------------
    // My Courses Page
    // -------------------------
    @GetMapping("/my-courses")
    public String myEnrolledCoursesPage() {
        return "student/my-courses";
    }

    // -------------------------
    // Profile Page
    // -------------------------
    @GetMapping("/profile")
    public String profilePage() {
        return "student/profile";
    }

    // -------------------------
    // View Lesson (Student)
    // -------------------------
    @GetMapping("/lesson/{lessonId}")
    public String lessonDetailPage(@PathVariable Long lessonId, Model model) {
        // Pass lessonId to Thymeleaf template
        model.addAttribute("lessonId", lessonId);
        // Optional: You can also add a placeholder for lessonTitle if needed
        model.addAttribute("lessonTitle", "Loading...");

        return "student/lesson-view-student";
    }

    // -------------------------
    // Course Lessons Page
    // -------------------------
    @GetMapping("/course-lessons/{courseId}")
    public String courseLessonsPage(@PathVariable Long courseId, Model model) {
        // Fetch course details using CourseService
        CourseResponse course = courseService.getCourseById(courseId);

        String courseTitle = (course != null) ? course.getTitle() : "Course Lessons";

        // Add attributes for Thymeleaf page
        model.addAttribute("courseId", courseId);
        model.addAttribute("courseTitle", courseTitle);

        return "student/course_lessons";
    }
}