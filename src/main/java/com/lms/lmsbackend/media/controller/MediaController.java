package com.lms.lmsbackend.media.controller;

import com.lms.lmsbackend.media.dto.MediaUploadResponse;
import com.lms.lmsbackend.media.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping("/upload/{lessonId}")
    public ResponseEntity<MediaUploadResponse> uploadMediaToLesson(
            @PathVariable Long lessonId,
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(mediaService.uploadMediaToLesson(lessonId, file));
    }
}