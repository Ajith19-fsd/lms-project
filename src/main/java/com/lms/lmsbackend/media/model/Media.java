package com.lms.lmsbackend.media.model;

import com.lms.lmsbackend.lesson.model.Lesson;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a media file.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "media_files")
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileUrl;
    private String fileType;

    // Link the media with a Lesson
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    @ToString.Exclude
    private Lesson lesson;
}