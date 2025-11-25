package com.lms.lmsbackend.lesson.repository;

import com.lms.lmsbackend.lesson.model.Lesson;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    @EntityGraph(attributePaths = {"mediaFiles"})
    List<Lesson> findByCourseId(Long courseId);

    @EntityGraph(attributePaths = {"mediaFiles"})
    Lesson findLessonById(Long id);
}