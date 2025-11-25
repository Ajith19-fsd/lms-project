package com.lms.lmsbackend.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * General web pages like home, login, signup, and redirect handling.
 */
@Controller
public class WebController {

    // Home page
    @GetMapping("/")
    public String homePage() {
        return "index";
    }

    // Login page
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // Signup page
    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    // Generic redirect page
    @GetMapping("/redirect-page")
    public String redirectPage() {
        return "redirect-page";
    }

    // Redirect after login based on role
    @GetMapping("/redirectAfterLogin")
    public String redirectAfterLogin(@RequestParam(required = false) String role) {
        if (role == null) return "redirect:/login";

        switch (role.toUpperCase()) {
            case "ADMIN":
                return "redirect:/admin/dashboard";
            case "INSTRUCTOR":
                return "redirect:/instructor/dashboard";
            case "STUDENT":
                return "redirect:/student/dashboard"; // Updated to go to student dashboard
            default:
                return "redirect:/login";
        }
    }
}