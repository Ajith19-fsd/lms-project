package com.lms.lmsbackend.frontend.controller;

import com.lms.lmsbackend.auth.model.User;
import com.lms.lmsbackend.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminPageController {

    private final UserRepository userRepository;

    // -------------------------
    // Dashboard Page
    // -------------------------
    @GetMapping("/dashboard")
    public String dashboardPage() {
        return "admin/dashboard_admin";
    }

    // -------------------------
    // Manage Courses Page
    // -------------------------
    @GetMapping("/manage-courses")
    public String manageCoursesPage() {
        return "admin/courses";
    }

    // -------------------------
    // Manage Users Page
    // -------------------------
    @GetMapping("/manage-users")
    public String manageUsersPage() {
        return "admin/users";
    }

    // -------------------------
    // API: Admin Profile
    // -------------------------
    @GetMapping("/api/profile")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getAdminProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (auth != null && auth.isAuthenticated()) ? auth.getName() : null;

        String fullName = "Admin";
        if (email != null) {
            User admin = userRepository.findByEmail(email).orElse(null);
            if (admin != null && admin.getFullName() != null && !admin.getFullName().isEmpty()) {
                fullName = admin.getFullName();
            }
        }

        Map<String, String> response = new HashMap<>();
        response.put("email", email);
        response.put("fullName", fullName);

        return ResponseEntity.ok(response);
    }
}