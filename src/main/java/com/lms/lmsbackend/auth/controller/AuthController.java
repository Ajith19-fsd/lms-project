package com.lms.lmsbackend.auth.controller;

import com.lms.lmsbackend.auth.dto.LoginRequest;
import com.lms.lmsbackend.auth.dto.LoginResponse;
import com.lms.lmsbackend.auth.dto.SignupRequest;
import com.lms.lmsbackend.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup/{role}")
    public ResponseEntity<LoginResponse> signup(
            @PathVariable("role") String role,
            @RequestBody SignupRequest request
    ) {
        if (role != null) request.setRole(role.toUpperCase());
        LoginResponse response = authService.registerUser(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.loginUser(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("Auth API OK");
    }
}
