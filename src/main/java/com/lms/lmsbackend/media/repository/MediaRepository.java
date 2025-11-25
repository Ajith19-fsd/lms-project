package com.lms.lmsbackend.media.repository;

import com.lms.lmsbackend.media.model.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Media entity.
 */
@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {
}