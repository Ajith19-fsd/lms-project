package com.lms.lmsbackend.course.repository;

import com.lms.lmsbackend.course.model.Course;
import com.lms.lmsbackend.course.model.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Fetch instructor courses along with lessons
    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.lessons WHERE c.instructor.id = :instructorId")
    List<Course> findByInstructorIdWithLessons(@Param("instructorId") Long instructorId);

    List<Course> findByInstructorId(Long instructorId);

    List<Course> findByStatus(CourseStatus status);

    List<Course> findByIdIn(List<Long> ids);

    boolean existsByInstructorIdAndTitle(Long instructorId, String title);
}