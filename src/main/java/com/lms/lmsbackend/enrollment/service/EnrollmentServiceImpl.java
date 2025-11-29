package com.lms.lmsbackend.enrollment.service;

import com.lms.lmsbackend.enrollment.dto.EnrollmentRequest;
import com.lms.lmsbackend.enrollment.dto.EnrollmentResponse;
import com.lms.lmsbackend.enrollment.model.Enrollment;
import com.lms.lmsbackend.enrollment.repository.EnrollmentRepository;
import com.lms.lmsbackend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    @Override
    public EnrollmentResponse enrollStudent(Long studentId, EnrollmentRequest request) {

        return enrollmentRepository
                .findByStudentIdAndCourseId(studentId, request.getCourseId())
                .map(existing -> {
                    // Already enrolled: return existing enrollment
                    return mapToResponse(existing);
                })
                .orElseGet(() -> {
                    // Not enrolled: create new enrollment
                    Enrollment enrollment = Enrollment.builder()
                            .studentId(studentId)
                            .courseId(request.getCourseId())
                            .enrolledAt(LocalDateTime.now())
                            .build();
                    Enrollment saved = enrollmentRepository.save(enrollment);
                    return mapToResponse(saved);
                });
    }

    @Override
    public List<EnrollmentResponse> getAllEnrollments() {
        return enrollmentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EnrollmentResponse getEnrollmentById(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Enrollment not found with ID: " + enrollmentId));
        return mapToResponse(enrollment);
    }

    @Override
    public List<EnrollmentResponse> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Enrollment not found with ID: " + enrollmentId));
        enrollmentRepository.delete(enrollment);
    }

    private EnrollmentResponse mapToResponse(Enrollment enrollment) {
        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getStudentId(),
                enrollment.getCourseId(),
                enrollment.getEnrolledAt()
        );
    }
}
