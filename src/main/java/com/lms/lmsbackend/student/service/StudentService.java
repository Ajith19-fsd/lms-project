package com.lms.lmsbackend.student.service;

import com.lms.lmsbackend.auth.model.User;
import com.lms.lmsbackend.auth.repository.UserRepository;
import com.lms.lmsbackend.course.model.Course;
import com.lms.lmsbackend.course.repository.CourseRepository;
import com.lms.lmsbackend.enrollment.model.Enrollment;
import com.lms.lmsbackend.enrollment.repository.EnrollmentRepository;
import com.lms.lmsbackend.exception.ResourceNotFoundException;
import com.lms.lmsbackend.lesson.dto.LessonResponse;
import com.lms.lmsbackend.lesson.service.LessonService;
import com.lms.lmsbackend.student.dto.EnrollmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final LessonService lessonService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // GET PROFILE
    public Map<String, String> getProfile(Long studentId) {
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Map<String, String> map = new HashMap<>();
        map.put("name", user.getFullName());
        map.put("email", user.getEmail());
        return map;
    }

    // UPDATE PROFILE
    public Map<String, String> updateProfile(Long studentId, String name, String email, String password) {
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (name != null && !name.isBlank()) user.setFullName(name);
        if (email != null && !email.isBlank()) user.setEmail(email);
        if (password != null && !password.isBlank()) user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);

        Map<String, String> res = new HashMap<>();
        res.put("message", "Profile updated successfully");
        res.put("name", user.getFullName());
        res.put("email", user.getEmail());
        return res;
    }

    // GET MY ENROLLMENTS
    public List<EnrollmentResponse> getMyEnrollments(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream()
                .map(e -> {
                    Course course = courseRepository.findById(e.getCourseId())
                            .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
                    return EnrollmentResponse.builder()
                            .courseId(course.getId())
                            .courseTitle(course.getTitle())
                            .instructorName(course.getInstructor().getFullName())
                            .enrollmentStatus("Enrolled")
                            .build();
                })
                .collect(Collectors.toList());
    }

    // DASHBOARD STATS
    public Map<String, Long> getDashboardStats(Long studentId) {
        long totalCourses = courseRepository.count();
        long enrolledCourses = enrollmentRepository.findByStudentId(studentId).size();

        long completedLessons = 0; // Placeholder
        long pendingLessons = 0;

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        for (Enrollment e : enrollments) {
            List<LessonResponse> lessons = lessonService.getLessonsByCourse(e.getCourseId());
            pendingLessons += lessons.size();
        }

        Map<String, Long> stats = new HashMap<>();
        stats.put("totalCourses", totalCourses);
        stats.put("enrolledCourses", enrolledCourses);
        stats.put("completedLessons", completedLessons);
        stats.put("pendingLessons", pendingLessons);

        return stats;
    }

    // GET COURSE BY ID
    public Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));
    }

    // GET SINGLE LESSON WITH MEDIA
    public LessonResponse getLessonWithMedia(Long lessonId) {
        LessonResponse lesson = lessonService.getLessonById(lessonId);
        if (lesson.getMediaFiles() == null) {
            lesson.setMediaFiles(new ArrayList<>()); // ensure empty list
        }
        return lesson;
    }

    // GET ALL LESSONS OF A COURSE
    public List<LessonResponse> getLessonsByCourse(Long courseId) {
        return lessonService.getLessonsByCourse(courseId);
    }
}