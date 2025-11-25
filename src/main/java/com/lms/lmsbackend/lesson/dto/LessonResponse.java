package com.lms.lmsbackend.lesson.dto;

import com.lms.lmsbackend.media.dto.MediaResponse;
import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LessonResponse {

    private Long id;
    private String title;
    private String content;
    private Long courseId;

    // Uploadcare single file URL
    private String fileUrl;

    // Legacy media list (still works)
    private List<MediaResponse> mediaFiles;
    private int mediaCount;
}