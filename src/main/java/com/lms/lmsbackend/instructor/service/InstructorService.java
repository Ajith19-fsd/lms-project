package com.lms.lmsbackend.instructor.service;

import com.lms.lmsbackend.auth.model.User;
import com.lms.lmsbackend.auth.repository.UserRepository;
import com.lms.lmsbackend.course.dto.CourseRequest;
import com.lms.lmsbackend.course.dto.CourseResponse;
import com.lms.lmsbackend.course.model.Course;
import com.lms.lmsbackend.course.repository.CourseRepository;
import com.lms.lmsbackend.course.service.CourseService;
import com.lms.lmsbackend.exception.ResourceNotFoundException;
import com.lms.lmsbackend.instructor.dto.InstructorProfileResponse;
import com.lms.lmsbackend.lesson.dto.LessonResponse;
import com.lms.lmsbackend.lesson.model.Lesson;
import com.lms.lmsbackend.media.dto.MediaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InstructorService {

    private final CourseService courseService;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    /** Get instructor ID by email (used in controller) */
    public Long getInstructorIdByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with email: " + email));
        return user.getId();
    }

    /** Fetch instructor profile as DTO */
    public InstructorProfileResponse getInstructorProfile(Long instructorId) {
        User user = userRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));

        String roleStr = user.getRole() != null && user.getRole().getName() != null
                ? user.getRole().getName().name() // single Role -> ERole -> String
                : "UNKNOWN";

        return new InstructorProfileResponse(
                user.getFullName(),
                user.getUsername(),
                user.getEmail(),
                roleStr
        );
    }

    public List<CourseResponse> getCoursesByInstructor(Long instructorId) {
        return courseRepository.findByInstructor_Id(instructorId)
                .stream()
                .map(this::mapCourseToResponse)
                .collect(Collectors.toList());
    }

    public CourseResponse addCourse(CourseRequest request, Long instructorId) {
        return courseService.addCourse(request, instructorId);
    }

    public CourseResponse updateCourse(Long id, CourseRequest request) {
        return courseService.updateCourse(id, request);
    }

    public void deleteCourse(Long id) {
        courseService.deleteCourse(id);
    }

    public void deleteCourseByInstructor(Long courseId, Long instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (!course.getInstructor().getId().equals(instructorId)) {
            throw new IllegalArgumentException("Not authorized to delete this course.");
        }

        courseRepository.delete(course);
    }

    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        return mapCourseToResponse(course);
    }

    public List<LessonResponse> getLessonsByCourse(Long courseId, Long instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (!course.getInstructor().getId().equals(instructorId)) {
            throw new IllegalArgumentException("Not authorized");
        }

        return course.getLessons() == null
                ? Collections.emptyList()
                : course.getLessons().stream()
                .map(this::mapLessonToResponse)
                .collect(Collectors.toList());
    }

    private LessonResponse mapLessonToResponse(Lesson lesson) {
        List<MediaResponse> mediaResponses = lesson.getMediaFiles() == null
                ? Collections.emptyList()
                : lesson.getMediaFiles().stream()
                .map(media -> new MediaResponse(
                        media.getId(),
                        media.getFileName(),
                        media.getFileType(),
                        media.getFileUrl()
                )).collect(Collectors.toList());

        return LessonResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .content(lesson.getContent())
                .courseId(lesson.getCourse().getId())
                .mediaFiles(mediaResponses)
                .build();
    }

    private CourseResponse mapCourseToResponse(Course course) {
        int lessonCount = course.getLessons() != null ? course.getLessons().size() : 0;
        User instructor = course.getInstructor();

        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getCategory(),
                course.getPrice(),
                instructor != null ? instructor.getId() : null,
                instructor != null ? instructor.getFullName() : "Unknown",
                course.getStatus(),
                lessonCount
        );
    }
}
