package com.lms.lmsbackend.media.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for returning media file info.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaResponse {

    private Long id;
    private String fileName;
    private String fileUrl;
    private String fileType;
}