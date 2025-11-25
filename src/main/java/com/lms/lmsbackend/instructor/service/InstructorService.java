package com.lms.lmsbackend.instructor.service;

import com.lms.lmsbackend.auth.model.User;
import com.lms.lmsbackend.auth.repository.UserRepository;
import com.lms.lmsbackend.course.dto.CourseRequest;
import com.lms.lmsbackend.course.dto.CourseResponse;
import com.lms.lmsbackend.course.model.Course;
import com.lms.lmsbackend.course.repository.CourseRepository;
import com.lms.lmsbackend.course.service.CourseService;
import com.lms.lmsbackend.exception.ResourceNotFoundException;
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

    // ✅ Get courses for instructor (eagerly load lessons)
    public List<CourseResponse> getCoursesByInstructor(Long instructorId) {
        List<Course> courses = courseRepository.findByInstructorIdWithLessons(instructorId);
        return courses.stream()
                .map(course -> mapCourseToResponse(course))
                .collect(Collectors.toList());
    }

    // ✅ Create a new course
    public CourseResponse addCourse(CourseRequest request, Long instructorId) {
        return courseService.addCourse(request, instructorId);
    }

    // ✅ Update course
    public CourseResponse updateCourse(Long id, CourseRequest request) {
        return courseService.updateCourse(id, request);
    }

    // ✅ Delete course
    public void deleteCourse(Long id) {
        courseService.deleteCourse(id);
    }

    // ✅ Delete course with instructor ownership check
    public void deleteCourseByInstructor(Long courseId, Long instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));

        if (course.getInstructor() == null || !course.getInstructor().getId().equals(instructorId)) {
            throw new IllegalArgumentException("You are not authorized to delete this course.");
        }

        courseRepository.delete(course);
    }

    // ✅ Get single course
    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + id));
        return mapCourseToResponse(course);
    }

    // ✅ Get instructor profile
    public User getInstructorProfile(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with ID: " + id));
    }

    // ✅ Map lesson entity → DTO
    public LessonResponse mapLessonToResponse(Lesson lesson) {
        List<MediaResponse> mediaResponses = lesson.getMediaFiles() != null
                ? lesson.getMediaFiles().stream()
                .map(media -> new MediaResponse(
                        media.getId(),
                        media.getFileName(),
                        media.getFileType(),
                        media.getFileUrl()
                ))
                .collect(Collectors.toList())
                : Collections.emptyList();

        return LessonResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .content(lesson.getContent())
                .courseId(lesson.getCourse() != null ? lesson.getCourse().getId() : null)
                .mediaFiles(mediaResponses)
                .build();
    }

    // ✅ Get lessons for a course with instructor check
    public List<LessonResponse> getLessonsByCourse(Long courseId, Long instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));

        if (course.getInstructor() == null || !course.getInstructor().getId().equals(instructorId)) {
            throw new IllegalArgumentException("You are not authorized to view lessons for this course.");
        }

        return course.getLessons() != null
                ? course.getLessons().stream().map(this::mapLessonToResponse).collect(Collectors.toList())
                : Collections.emptyList();
    }

    // ✅ Map course entity → DTO including totalLessons count
    private CourseResponse mapCourseToResponse(Course course) {
        int lessonsCount = course.getLessons() != null ? course.getLessons().size() : 0;
        User instructor = course.getInstructor();
        String instructorName = instructor != null && instructor.getFullName() != null
                ? instructor.getFullName()
                : "Unknown Instructor";

        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getCategory(),
                course.getPrice(),
                instructor != null ? instructor.getId() : null,
                instructorName,
                course.getStatus(),
                lessonsCount
        );
    }
}