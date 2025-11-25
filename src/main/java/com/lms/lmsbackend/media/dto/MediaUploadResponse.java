package com.lms.lmsbackend.media.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Response returned after media upload.
 */
@Getter
@Setter
@AllArgsConstructor
public class MediaUploadResponse {
    private Long mediaId;
    private String fileUrl;
    private Long lessonId;
}