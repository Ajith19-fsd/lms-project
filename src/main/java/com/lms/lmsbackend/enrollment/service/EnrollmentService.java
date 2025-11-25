package com.lms.lmsbackend.enrollment.service;

import com.lms.lmsbackend.enrollment.dto.EnrollmentRequest;
import com.lms.lmsbackend.enrollment.dto.EnrollmentResponse;

import java.util.List;

public interface EnrollmentService {

    EnrollmentResponse enrollStudent(Long studentId, EnrollmentRequest request);

    List<EnrollmentResponse> getAllEnrollments();

    EnrollmentResponse getEnrollmentById(Long enrollmentId);

    List<EnrollmentResponse> getEnrollmentsByStudent(Long studentId);

    void deleteEnrollment(Long enrollmentId);
}