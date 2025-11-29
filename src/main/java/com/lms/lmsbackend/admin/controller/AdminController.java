package com.lms.lmsbackend.admin.controller;

import com.lms.lmsbackend.admin.dto.AdminStatsResponse;
import com.lms.lmsbackend.admin.dto.ChangeUserRoleRequest;
import com.lms.lmsbackend.admin.dto.CourseApprovalRequest;
import com.lms.lmsbackend.admin.dto.UserResponse;
import com.lms.lmsbackend.admin.service.AdminService;
import com.lms.lmsbackend.course.dto.CourseResponse;
import com.lms.lmsbackend.auth.model.Role;
import com.lms.lmsbackend.auth.repository.UserRepository;
import com.lms.lmsbackend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;

    // =========================
    // Approve / Reject Course
    // =========================
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/course/approve")
    public ResponseEntity<Map<String, String>> approveCourse(@RequestBody CourseApprovalRequest request) {
        adminService.approveCourse(request);
        return ResponseEntity.ok(Map.of(
                "message", request.isApproved() ? "Course approved successfully!" : "Course rejected successfully!"
        ));
    }

    // =========================
    // Get all users
    // =========================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsers() {
        return ResponseEntity.ok(adminService.getUsers());
    }

    // =========================
    // Change user role
    // =========================
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/user/change-role")
    public ResponseEntity<Map<String, String>> changeUserRole(@RequestBody ChangeUserRoleRequest request) {
        adminService.changeUserRole(request);
        return ResponseEntity.ok(Map.of("message", "User role updated successfully!"));
    }

    // =========================
    // Get courses (optional filter)
    // =========================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/courses")
    public ResponseEntity<List<CourseResponse>> getCourses(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(adminService.getCourses(status));
    }

    // =========================
    // Delete course
    // =========================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/course/{courseId}")
    public ResponseEntity<Map<String, String>> deleteCourse(@PathVariable Long courseId) {
        adminService.deleteCourse(courseId);
        return ResponseEntity.ok(Map.of("message", "Course deleted successfully!"));
    }

    // =========================
    // Delete user (Admin cannot be deleted)
    // =========================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long userId) {

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (user.getRole() != null && user.getRole().getName() == Role.ERole.ADMIN) {
            return ResponseEntity.badRequest().body(Map.of("message", "Cannot delete ADMIN users!"));
        }

        adminService.deleteUser(userId);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully!"));
    }

    // =========================
    // Admin dashboard stats
    // =========================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats() {
        AdminStatsResponse stats = adminService.getStats();
        return ResponseEntity.ok(stats);
    }
}
