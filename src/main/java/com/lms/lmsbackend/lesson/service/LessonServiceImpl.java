package com.lms.lmsbackend.lesson.service;

import com.lms.lmsbackend.course.model.Course;
import com.lms.lmsbackend.course.repository.CourseRepository;
import com.lms.lmsbackend.exception.ResourceNotFoundException;
import com.lms.lmsbackend.lesson.dto.LessonRequest;
import com.lms.lmsbackend.lesson.dto.LessonResponse;
import com.lms.lmsbackend.lesson.model.Lesson;
import com.lms.lmsbackend.lesson.repository.LessonRepository;
import com.lms.lmsbackend.media.dto.MediaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;

    @Override
    public LessonResponse addLesson(LessonRequest request) {

        Long courseId = request.getCourseId();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Course not found with ID: " + courseId)
                );

        Lesson lesson = Lesson.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .fileUrl(request.getFileUrl())  // Uploadcare file
                .course(course)
                .build();

        return toResponse(lessonRepository.save(lesson));
    }

    @Override
    public LessonResponse getLessonById(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found: " + id));

        return toResponse(lesson);
    }

    @Override
    public List<LessonResponse> getLessonsByCourse(Long courseId) {
        return lessonRepository.findByCourseId(courseId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LessonResponse updateLesson(Long id, LessonRequest request) {

        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found: " + id));

        if (request.getTitle() != null) lesson.setTitle(request.getTitle());
        if (request.getContent() != null) lesson.setContent(request.getContent());
        if (request.getFileUrl() != null) lesson.setFileUrl(request.getFileUrl());

        return toResponse(lessonRepository.save(lesson));
    }

    @Override
    public void deleteLesson(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found: " + id));

        lessonRepository.delete(lesson);
    }

    private LessonResponse toResponse(Lesson lesson) {

        List<MediaResponse> mediaList =
                lesson.getMediaFiles() == null ? List.of() :
                        lesson.getMediaFiles().stream()
                                .map(m -> MediaResponse.builder()
                                        .id(m.getId())
                                        .fileName(m.getFileName())
                                        .fileUrl(m.getFileUrl())
                                        .fileType(m.getFileType())
                                        .build())
                                .collect(Collectors.toList());

        return LessonResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .content(lesson.getContent())
                .courseId(lesson.getCourse().getId())
                .fileUrl(lesson.getFileUrl())
                .mediaFiles(mediaList)
                .mediaCount(mediaList.size())
                .build();
    }
}