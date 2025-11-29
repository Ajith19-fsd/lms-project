package com.lms.lmsbackend.instructor.controller;

import com.lms.lmsbackend.course.dto.CourseResponse;
import com.lms.lmsbackend.instructor.dto.InstructorProfileResponse;
import com.lms.lmsbackend.instructor.service.InstructorService;
import com.lms.lmsbackend.lesson.dto.LessonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructor")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InstructorController {

    private final InstructorService instructorService;

    // Get instructor ID from JWT Authentication
    private Long getInstructorId(Authentication authentication) {
        String email = authentication.getName();
        return instructorService.getInstructorIdByEmail(email);
    }

    // Fetch instructor profile
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/profile")
    public ResponseEntity<InstructorProfileResponse> getProfile(Authentication authentication) {
        Long instructorId = getInstructorId(authentication);
        InstructorProfileResponse profile = instructorService.getInstructorProfile(instructorId);
        return ResponseEntity.ok(profile);
    }

    // Fetch all courses for the instructor
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/courses")
    public ResponseEntity<List<CourseResponse>> getCourses(Authentication authentication) {
        Long instructorId = getInstructorId(authentication);
        return ResponseEntity.ok(instructorService.getCoursesByInstructor(instructorId));
    }

    // Fetch course stats (total courses & lessons)
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/courses/count")
    public ResponseEntity<StatsResponse> getStats(Authentication authentication) {
        Long instructorId = getInstructorId(authentication);
        List<CourseResponse> courses = instructorService.getCoursesByInstructor(instructorId);

        int totalCourses = courses.size();
        int totalLessons = courses.stream()
                .mapToInt(CourseResponse::getTotalLessons)
                .sum();

        return ResponseEntity.ok(new StatsResponse(totalCourses, totalLessons));
    }

    // Fetch lessons for a specific course
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/courses/{courseId}/lessons")
    public ResponseEntity<List<LessonResponse>> getLessons(
            @PathVariable Long courseId,
            Authentication authentication
    ) {
        Long instructorId = getInstructorId(authentication);
        return ResponseEntity.ok(instructorService.getLessonsByCourse(courseId, instructorId));
    }

    // DTO for total courses & lessons
    public record StatsResponse(int totalCourses, int totalLessons) {}
}
