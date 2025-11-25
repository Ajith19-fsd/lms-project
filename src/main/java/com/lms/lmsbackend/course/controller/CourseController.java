package com.lms.lmsbackend.course.controller;

import com.lms.lmsbackend.auth.model.User;
import com.lms.lmsbackend.auth.repository.UserRepository;
import com.lms.lmsbackend.course.dto.CourseRequest;
import com.lms.lmsbackend.course.dto.CourseResponse;
import com.lms.lmsbackend.course.service.CourseService;
import com.lms.lmsbackend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CourseController {

    private final CourseService courseService;
    private final UserRepository userRepository;

    // ⛔ DO NOT CAST authentication.getPrincipal() into User
    // ⛔ Fix → always fetch User from DB using email

    private Long getCurrentUserId(Authentication authentication) {
        String email = authentication.getName();  // Spring Security username = email
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"))
                .getId();
    }

    // ===========================
    // Instructor: Add new course
    // ===========================
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping
    public ResponseEntity<CourseResponse> addCourse(
            @RequestBody CourseRequest request,
            Authentication authentication
    ) {
        Long instructorId = getCurrentUserId(authentication);
        CourseResponse savedCourse = courseService.addCourse(request, instructorId);
        return ResponseEntity.ok(savedCourse);
    }

    // ===========================
    // Instructor: Update course
    // ===========================
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long id,
            @RequestBody CourseRequest request,
            Authentication authentication
    ) {
        Long instructorId = getCurrentUserId(authentication);
        CourseResponse existing = courseService.getCourseById(id);

        if (!existing.getInstructorId().equals(instructorId)) {
            throw new ResourceNotFoundException("You are not allowed to update this course!");
        }

        return ResponseEntity.ok(courseService.updateCourse(id, request));
    }

    // ===========================
    // Instructor: Delete course
    // ===========================
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long instructorId = getCurrentUserId(authentication);
        CourseResponse existing = courseService.getCourseById(id);

        if (!existing.getInstructorId().equals(instructorId)) {
            throw new ResourceNotFoundException("You are not allowed to delete this course!");
        }

        courseService.deleteCourse(id);
        return ResponseEntity.ok("Course deleted successfully!");
    }

    // ===========================
    // Instructor: Get own courses
    // ===========================
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/instructor")
    public ResponseEntity<List<CourseResponse>> getCoursesByInstructor(
            Authentication authentication
    ) {
        Long instructorId = getCurrentUserId(authentication);
        return ResponseEntity.ok(courseService.getCoursesByInstructor(instructorId));
    }

    // ===========================
    // Admin: Get all courses
    // ===========================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    // ===========================
    // Public: Approved courses
    // ===========================
    @GetMapping("/approved")
    public ResponseEntity<List<CourseResponse>> getApprovedCourses() {
        return ResponseEntity.ok(courseService.getApprovedCourses());
    }

    // ===========================
    // Public: Course by ID
    // ===========================
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }
}