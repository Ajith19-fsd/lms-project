package com.lms.lmsbackend.frontend.controller;

import com.lms.lmsbackend.course.dto.CourseResponse;
import com.lms.lmsbackend.instructor.service.InstructorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/instructor")
public class InstructorPageController {

    private final InstructorService instructorService;

    // -------------------------
    // Dashboard Page
    // -------------------------
    @GetMapping("/dashboard")
    public String dashboardPage() {
        return "instructor/dashboard_instructor";
    }

    // -------------------------
    // My Courses
    // -------------------------
    @GetMapping("/my-courses")
    public String myCoursesPage() {
        return "instructor/my-courses";
    }

    @GetMapping("/create-course")
    public String createCoursePage() {
        return "instructor/create-course";
    }

    @GetMapping("/edit-course/{courseId}")
    public String editCoursePage(@PathVariable Long courseId, Model model) {
        model.addAttribute("courseId", courseId);
        return "instructor/edit-course";
    }

    @GetMapping("/view-course/{courseId}")
    public String viewCoursePage(@PathVariable Long courseId, Model model) {
        CourseResponse course = instructorService.getCourseById(courseId);
        model.addAttribute("course", course);
        return "instructor/view-course";
    }

    @GetMapping("/create-lesson")
    public String createLessonPage() {
        return "instructor/create-lesson";
    }

    @GetMapping("/view-lessons/{courseId}")
    public String viewLessonsPage(@PathVariable Long courseId, Model model) {
        CourseResponse course = instructorService.getCourseById(courseId);
        model.addAttribute("course", course);
        return "instructor/view-lessons";
    }

    @GetMapping("/view-lesson/{lessonId}")
    public String viewLessonPage(@PathVariable Long lessonId, Model model) {
        model.addAttribute("lessonId", lessonId);
        return "instructor/view-lesson";
    }
}