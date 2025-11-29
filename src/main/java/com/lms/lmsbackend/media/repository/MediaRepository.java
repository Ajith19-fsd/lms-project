package com.lms.lmsbackend.media.repository;

import com.lms.lmsbackend.media.model.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

    // Fetch all media by lesson
    List<Media> findByLessonId(Long lessonId);

    // Delete media belonging to a lesson
    void deleteByLessonId(Long lessonId);
}
