package com.lms.lmsbackend.student.controller;

import com.lms.lmsbackend.auth.model.User;
import com.lms.lmsbackend.auth.repository.UserRepository;
import com.lms.lmsbackend.course.model.Course;
import com.lms.lmsbackend.lesson.dto.LessonResponse;
import com.lms.lmsbackend.student.dto.EnrollmentResponse;
import com.lms.lmsbackend.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final UserRepository userRepository;

    @GetMapping("/profile")
    public ResponseEntity<Map<String, String>> getProfile(Authentication auth) {
        User user = getUserFromAuth(auth);
        return ResponseEntity.ok(studentService.getProfile(user.getId()));
    }

    @PutMapping("/profile")
    public ResponseEntity<Map<String, String>> updateProfile(Authentication auth,
                                                             @RequestBody Map<String, String> request) {
        User user = getUserFromAuth(auth);
        return ResponseEntity.ok(studentService.updateProfile(user.getId(),
                request.get("name"), request.get("email"), request.get("password")));
    }

    @GetMapping("/my-courses")
    public ResponseEntity<List<EnrollmentResponse>> getMyCourses(Authentication auth) {
        User user = getUserFromAuth(auth);
        return ResponseEntity.ok(studentService.getMyEnrollments(user.getId()));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Long>> getDashboardStats(Authentication auth) {
        User user = getUserFromAuth(auth);
        return ResponseEntity.ok(studentService.getDashboardStats(user.getId()));
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<Course> getCourseDetail(@PathVariable Long courseId) {
        return ResponseEntity.ok(studentService.getCourseById(courseId));
    }

    @GetMapping("/courses/{courseId}/lessons")
    public ResponseEntity<List<LessonResponse>> getCourseLessons(@PathVariable Long courseId) {
        return ResponseEntity.ok(studentService.getLessonsByCourse(courseId));
    }

    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<LessonResponse> getLesson(@PathVariable Long lessonId) {
        return ResponseEntity.ok(studentService.getLessonWithMedia(lessonId));
    }

    private User getUserFromAuth(Authentication auth) {
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}