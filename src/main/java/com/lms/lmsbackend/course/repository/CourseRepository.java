package com.lms.lmsbackend.course.repository;

import com.lms.lmsbackend.course.model.Course;
import com.lms.lmsbackend.course.model.CourseStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Filter by status
    List<Course> findByStatus(CourseStatus status);

    // Simple find by instructor id
    List<Course> findByInstructorId(Long instructorId);

    // Fetch courses + lessons for instructor dashboard (eager-load lessons)
    @EntityGraph(attributePaths = {"lessons"})
    List<Course> findByInstructor_Id(Long instructorId);
}
