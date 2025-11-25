package com.lms.lmsbackend.instructor.controller;

import com.lms.lmsbackend.auth.model.User;
import com.lms.lmsbackend.auth.repository.UserRepository;
import com.lms.lmsbackend.course.dto.CourseResponse;
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
    private final UserRepository userRepository;

    // Helper: get instructor ID from JWT authentication
    private Long getInstructorId(Authentication authentication) {
        String email = authentication.getName(); // JWT username = email
        return userRepository.findByEmail(email)
                .orElseThrow()
                .getId();
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(Authentication authentication) {
        Long instructorId = getInstructorId(authentication);
        return ResponseEntity.ok(instructorService.getInstructorProfile(instructorId));
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/courses")
    public ResponseEntity<List<CourseResponse>> getCourses(Authentication authentication) {
        Long instructorId = getInstructorId(authentication);
        return ResponseEntity.ok(instructorService.getCoursesByInstructor(instructorId));
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/courses/count")
    public ResponseEntity<StatsResponse> getStats(Authentication authentication) {
        Long instructorId = getInstructorId(authentication);

        // Fetch courses including lessons
        List<CourseResponse> courses = instructorService.getCoursesByInstructor(instructorId);

        int totalCourses = courses.size();
        int totalLessons = courses.stream()
                .mapToInt(CourseResponse::getTotalLessons) // ✅ Use getter method
                .sum();

        return ResponseEntity.ok(new StatsResponse(totalCourses, totalLessons));
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/courses/{courseId}/lessons")
    public ResponseEntity<List<LessonResponse>> getLessons(
            @PathVariable Long courseId,
            Authentication authentication
    ) {
        Long instructorId = getInstructorId(authentication);
        return ResponseEntity.ok(instructorService.getLessonsByCourse(courseId, instructorId));
    }

    // Record DTO for total courses & lessons
    public record StatsResponse(int totalCourses, int totalLessons) {}
}