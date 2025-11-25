package com.lms.lmsbackend.exception;

/**
 * Custom exception for unauthorized access scenarios.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}