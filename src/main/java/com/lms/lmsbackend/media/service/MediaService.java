package com.lms.lmsbackend.media.service;

import com.lms.lmsbackend.exception.ResourceNotFoundException;
import com.lms.lmsbackend.lesson.model.Lesson;
import com.lms.lmsbackend.lesson.repository.LessonRepository;
import com.lms.lmsbackend.media.dto.MediaUploadResponse;
import com.lms.lmsbackend.media.model.Media;
import com.lms.lmsbackend.media.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository mediaRepository;
    private final LessonRepository lessonRepository;

    /**
     * Upload a media file and attach it to a lesson.
     * Since you are storing PDFs manually in static/media/lessons,
     * we only save the reference URL.
     */
    public MediaUploadResponse uploadMediaToLesson(Long lessonId, MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Uploaded file cannot be empty");
        }

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with ID: " + lessonId));

        // Extract filename (keep as uploaded)
        String fileName = file.getOriginalFilename() != null
                ? file.getOriginalFilename().trim()
                : "unknown-file";

        // CORRECT URL (served by Spring Boot automatically)
        String fileUrl = "/media/lessons/" + fileName;

        Media media = Media.builder()
                .fileName(fileName)
                .fileUrl(fileUrl)
                .fileType(file.getContentType())
                .lesson(lesson)
                .build();

        Media savedMedia = mediaRepository.save(media);

        return new MediaUploadResponse(
                savedMedia.getId(),
                savedMedia.getFileUrl(),
                lesson.getId()
        );
    }
}