package com.lms.lmsbackend.enrollment.controller;

import com.lms.lmsbackend.enrollment.dto.EnrollmentRequest;
import com.lms.lmsbackend.enrollment.dto.EnrollmentResponse;
import com.lms.lmsbackend.enrollment.service.EnrollmentService;
import com.lms.lmsbackend.auth.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollment")
@RequiredArgsConstructor
@Tag(name = "Enrollment Management", description = "APIs for managing student enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final AuthUtil authUtil;

    @Operation(summary = "Enroll in a course")
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/enroll")
    public ResponseEntity<EnrollmentResponse> enrollStudent(@RequestBody EnrollmentRequest request) {
        Long studentId = authUtil.getCurrentUserId();
        EnrollmentResponse response = enrollmentService.enrollStudent(studentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get all enrollments")
    @PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR')")
    @GetMapping("/all")
    public ResponseEntity<List<EnrollmentResponse>> getAllEnrollments() {
        List<EnrollmentResponse> enrollments = enrollmentService.getAllEnrollments();
        return ResponseEntity.ok(enrollments);
    }

    @Operation(summary = "Get enrollments of logged-in student")
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/my-courses")
    public ResponseEntity<List<EnrollmentResponse>> getMyEnrollments() {
        Long studentId = authUtil.getCurrentUserId();
        List<EnrollmentResponse> enrollments = enrollmentService.getEnrollmentsByStudent(studentId);
        return ResponseEntity.ok(enrollments);
    }

    @Operation(summary = "Get enrollment by ID")
    @PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR','STUDENT')")
    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponse> getEnrollmentById(@PathVariable Long id) {
        EnrollmentResponse enrollment = enrollmentService.getEnrollmentById(id);
        return ResponseEntity.ok(enrollment);
    }

    @Operation(summary = "Delete enrollment")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEnrollment(@PathVariable Long id) {
        enrollmentService.deleteEnrollment(id);
        return ResponseEntity.ok("Enrollment deleted successfully");
    }
}