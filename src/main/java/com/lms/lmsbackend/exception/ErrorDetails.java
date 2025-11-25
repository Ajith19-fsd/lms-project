package com.lms.lmsbackend.exception;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard structure for API error responses.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetails {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}