package com.lms.lmsbackend.course.service;

import com.lms.lmsbackend.auth.model.User;
import com.lms.lmsbackend.auth.repository.UserRepository;
import com.lms.lmsbackend.course.dto.CourseRequest;
import com.lms.lmsbackend.course.dto.CourseResponse;
import com.lms.lmsbackend.course.model.Course;
import com.lms.lmsbackend.course.model.CourseStatus;
import com.lms.lmsbackend.course.repository.CourseRepository;
import com.lms.lmsbackend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Override
    public CourseResponse addCourse(CourseRequest request, Long instructorId) {
        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with ID: " + instructorId));

        Course course = Course.builder()
                .title(request.getTitle().trim())
                .description(request.getDescription().trim())
                .category(request.getCategory() != null ? request.getCategory().trim() : "General")
                .price(request.getPrice() != null ? request.getPrice() : 0.0)
                .status(CourseStatus.PENDING)
                .instructor(instructor)
                .build();

        return mapToResponse(courseRepository.save(course));
    }

    @Override
    public CourseResponse updateCourse(Long id, CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + id));

        course.setTitle(request.getTitle().trim());
        course.setDescription(request.getDescription().trim());
        course.setCategory(request.getCategory().trim());
        course.setPrice(request.getPrice());

        return mapToResponse(courseRepository.save(course));
    }

    @Override
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + id));
        courseRepository.delete(course);
    }

    @Override
    public List<CourseResponse> getCoursesByInstructor(Long instructorId) {
        return courseRepository.findByInstructorIdWithLessons(instructorId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseResponse> getApprovedCourses() {
        return courseRepository.findByStatus(CourseStatus.APPROVED)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + id));

        return mapToResponse(course);
    }

    private CourseResponse mapToResponse(Course course) {
        User instructor = course.getInstructor();
        String instructorName = instructor != null && instructor.getFullName() != null
                ? instructor.getFullName()
                : "Unknown Instructor";

        int lessonsCount = course.getLessons() != null ? course.getLessons().size() : 0;

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