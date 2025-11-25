package com.lms.lmsbackend.admin.service;

import com.lms.lmsbackend.admin.dto.ChangeUserRoleRequest;
import com.lms.lmsbackend.admin.dto.CourseApprovalRequest;
import com.lms.lmsbackend.admin.dto.UserResponse;
import com.lms.lmsbackend.admin.dto.AdminStatsResponse;
import com.lms.lmsbackend.auth.model.Role;
import com.lms.lmsbackend.auth.model.User;
import com.lms.lmsbackend.auth.repository.RoleRepository;
import com.lms.lmsbackend.auth.repository.UserRepository;
import com.lms.lmsbackend.course.dto.CourseResponse;
import com.lms.lmsbackend.course.model.Course;
import com.lms.lmsbackend.course.model.CourseStatus;
import com.lms.lmsbackend.course.repository.CourseRepository;
import com.lms.lmsbackend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CourseRepository courseRepository;

    // ============================
    // NEW: GET ADMIN DASHBOARD STATS
    // ============================
    public AdminStatsResponse getStats() {
        long totalCourses = courseRepository.count();
        long totalUsers = userRepository.count();

        // compute instructor / student counts defensively without relying on custom repo methods
        long totalInstructors = userRepository.findAll().stream()
                .filter(u -> u.getRole() != null && u.getRole().getName() == Role.ERole.INSTRUCTOR)
                .count();

        long totalStudents = userRepository.findAll().stream()
                .filter(u -> u.getRole() != null && u.getRole().getName() == Role.ERole.STUDENT)
                .count();

        return new AdminStatsResponse(totalUsers, totalInstructors, totalStudents, totalCourses);
    }

    // ============================
    // APPROVE / REJECT COURSE
    // ============================
    public void approveCourse(CourseApprovalRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Course not found with ID: " + request.getCourseId()));

        course.setStatus(request.isApproved() ? CourseStatus.APPROVED : CourseStatus.REJECTED);
        courseRepository.save(course);
    }

    // ============================
    // GET ALL USERS (For Admin UI)
    // ============================
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(
                        user.getId(),

                        // FIX: Use real name — NOT Spring Security username()
                        user.getFullName() != null
                                ? user.getFullName()
                                : user.getUsername(),

                        user.getEmail(),

                        user.getRole() != null
                                ? user.getRole().getName().name()
                                : "UNKNOWN"
                ))
                .toList();
    }

    // ============================
    // CHANGE USER ROLE
    // ============================
    public void changeUserRole(ChangeUserRoleRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

        Role.ERole newRoleEnum;
        try {
            newRoleEnum = Role.ERole.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Invalid role: " + request.getRole());
        }

        Role newRole = roleRepository.findByName(newRoleEnum)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role not found: " + newRoleEnum));

        user.setRole(newRole);
        userRepository.save(user);
    }

    // ============================
    // GET COURSES (FILTER BY STATUS)
    // ============================
    public List<CourseResponse> getCourses(String status) {

        List<Course> courses;

        if (status == null || status.equalsIgnoreCase("ALL")) {
            courses = courseRepository.findAll();
        } else {

            CourseStatus courseStatus;
            try {
                courseStatus = CourseStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ResourceNotFoundException("Invalid course status: " + status);
            }

            courses = courseRepository.findByStatus(courseStatus);
        }

        return courses.stream()
                .map(this::mapToCourseResponse)
                .toList();
    }

    // ============================
    // MAP Course → CourseResponse DTO
    // ============================
    private CourseResponse mapToCourseResponse(Course course) {

        int lessonsCount = (course.getLessons() != null)
                ? course.getLessons().size()
                : 0;

        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getCategory(),
                course.getPrice(),
                course.getInstructor() != null ? course.getInstructor().getId() : null,
                course.getInstructor() != null ? course.getInstructor().getFullName() : "Unknown Instructor",
                course.getStatus(),
                lessonsCount
        );
    }

    // ============================
    // DELETE COURSE
    // ============================
    public void deleteCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Course not found with ID: " + courseId));
        courseRepository.delete(course);
    }
}
