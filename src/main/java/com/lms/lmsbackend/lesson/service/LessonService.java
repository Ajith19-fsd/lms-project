package com.lms.lmsbackend.lesson.service;

import com.lms.lmsbackend.lesson.dto.LessonRequest;
import com.lms.lmsbackend.lesson.dto.LessonResponse;

import java.util.List;

public interface LessonService {

    LessonResponse addLesson(LessonRequest request);

    LessonResponse getLessonById(Long id);

    List<LessonResponse> getLessonsByCourse(Long courseId);

    LessonResponse updateLesson(Long id, LessonRequest request);

    void deleteLesson(Long id);
}