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
import com.lms.lmsbackend.enrollment.repository.EnrollmentRepository;
import com.lms.lmsbackend.enrollment.model.Enrollment;
import com.lms.lmsbackend.exception.ResourceNotFoundException;
import com.lms.lmsbackend.lesson.model.Lesson;
import com.lms.lmsbackend.lesson.repository.LessonRepository;
import com.lms.lmsbackend.media.model.Media;
import com.lms.lmsbackend.media.repository.MediaRepository;
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
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final MediaRepository mediaRepository;

    // ============================
    // DASHBOARD STATS
    // ============================
    public AdminStatsResponse getStats() {
        long totalCourses = courseRepository.count();
        long totalUsers = userRepository.count();
        long totalInstructors = userRepository.findAll().stream()
                .filter(u -> u.getRole() != null && u.getRole().getName().equals(Role.ERole.INSTRUCTOR))
                .count();
        long totalStudents = userRepository.findAll().stream()
                .filter(u -> u.getRole() != null && u.getRole().getName().equals(Role.ERole.STUDENT))
                .count();

        return new AdminStatsResponse(totalUsers, totalInstructors, totalStudents, totalCourses);
    }

    // ============================
    // APPROVE / REJECT COURSE
    // ============================
    public void approveCourse(CourseApprovalRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + request.getCourseId()));

        course.setStatus(request.isApproved() ? CourseStatus.APPROVED : CourseStatus.REJECTED);
        courseRepository.save(course);
    }

    // ============================
    // GET ALL USERS
    // ============================
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getFullName() != null ? user.getFullName() : user.getUsername(),
                        user.getEmail(),
                        user.getRole() != null ? user.getRole().getName().name() : "UNKNOWN"
                ))
                .toList();
    }

    // ============================
    // CHANGE USER ROLE
    // ============================
    public void changeUserRole(ChangeUserRoleRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

        Role.ERole newRoleEnum;
        try {
            newRoleEnum = Role.ERole.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Invalid role: " + request.getRole());
        }

        Role newRole = roleRepository.findByName(newRoleEnum)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + newRoleEnum));

        user.setRole(newRole);
        userRepository.save(user);
    }

    // ============================
    // GET COURSES (OPTIONAL STATUS FILTER)
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

    private CourseResponse mapToCourseResponse(Course course) {
        int lessonsCount = (course.getLessons() != null) ? course.getLessons().size() : 0;
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
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));

        // Delete lessons and their media first
        List<Lesson> lessons = lessonRepository.findByCourseId(courseId);
        if (lessons != null && !lessons.isEmpty()) {
            for (Lesson lesson : lessons) {
                // delete media linked to lesson
                if (lesson.getMediaFiles() != null && !lesson.getMediaFiles().isEmpty()) {
                    // if MediaRepository stores Media entities, delete them
                    mediaRepository.deleteAll(lesson.getMediaFiles());
                }
            }
            lessonRepository.deleteAll(lessons);
        }

        // remove enrollments for this course
        List<com.lms.lmsbackend.enrollment.model.Enrollment> enrollmentsForCourse = enrollmentRepository.findAll()
                .stream()
                .filter(e -> e.getCourseId().equals(courseId))
                .toList();
        if (!enrollmentsForCourse.isEmpty()) {
            enrollmentRepository.deleteAll(enrollmentsForCourse);
        }

        courseRepository.delete(course);
    }

    // ============================
    // DELETE USER
    // ============================
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (user.getRole() != null && user.getRole().getName().equals(Role.ERole.ADMIN)) {
            throw new IllegalArgumentException("Cannot delete an ADMIN user!");
        }

        // 1) If student -> delete enrollments
        if (user.getRole() != null && user.getRole().getName().equals(Role.ERole.STUDENT)) {
            List<Enrollment> studentEnrollments = enrollmentRepository.findByStudentId(userId);
            if (studentEnrollments != null && !studentEnrollments.isEmpty()) {
                enrollmentRepository.deleteAll(studentEnrollments);
            }
        }

        // 2) If instructor -> delete all their courses (and lessons/media/enrollments)
        if (user.getRole() != null && user.getRole().getName().equals(Role.ERole.INSTRUCTOR)) {
            List<Course> instructorCourses = courseRepository.findByInstructorId(userId);
            if (instructorCourses != null && !instructorCourses.isEmpty()) {
                for (Course course : instructorCourses) {
                    Long courseId = course.getId();
                    // delete lessons and medias (deleteCourse already handles lessons/media/enrollments)
                    deleteCourse(courseId);
                }
            }
        }

        // 3) Finally delete the user
        userRepository.delete(user);
    }
}
