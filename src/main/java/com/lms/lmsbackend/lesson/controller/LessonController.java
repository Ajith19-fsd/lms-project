package com.lms.lmsbackend.lesson.controller;

import com.lms.lmsbackend.lesson.dto.LessonRequest;
import com.lms.lmsbackend.lesson.dto.LessonResponse;
import com.lms.lmsbackend.lesson.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    // CREATE LESSON (Instructor)
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping("/course/{courseId}")
    public ResponseEntity<LessonResponse> addLesson(
            @PathVariable Long courseId,
            @RequestBody LessonRequest request
    ) {
        request.setCourseId(courseId);
        return ResponseEntity.ok(lessonService.addLesson(request));
    }

    // GET Lesson by ID
    @GetMapping("/{id}")
    public ResponseEntity<LessonResponse> getLessonById(@PathVariable Long id) {
        return ResponseEntity.ok(lessonService.getLessonById(id));
    }

    // GET all lessons of a course
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<LessonResponse>> getLessonsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(lessonService.getLessonsByCourse(courseId));
    }

    // UPDATE
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PutMapping("/{id}")
    public ResponseEntity<LessonResponse> updateLesson(
            @PathVariable Long id,
            @RequestBody LessonRequest request
    ) {
        return ResponseEntity.ok(lessonService.updateLesson(id, request));
    }

    // DELETE
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.ok("Lesson deleted successfully.");
    }
}