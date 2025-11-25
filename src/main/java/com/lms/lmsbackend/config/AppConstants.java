package com.lms.lmsbackend.config;

/**
 * Global application constants used across the LMS system.
 */
public final class AppConstants {

    private AppConstants() {
        // Prevent instantiation
    }

    // 🔐 Role Constants
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_INSTRUCTOR = "INSTRUCTOR";
    public static final String ROLE_STUDENT = "STUDENT";

    // 🔑 JWT Header Constants
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
}