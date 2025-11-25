package com.lms.lmsbackend.auth.service;

import com.lms.lmsbackend.auth.dto.LoginRequest;
import com.lms.lmsbackend.auth.dto.LoginResponse;
import com.lms.lmsbackend.auth.dto.SignupRequest;
import com.lms.lmsbackend.auth.model.Role;
import com.lms.lmsbackend.auth.model.User;
import com.lms.lmsbackend.auth.repository.RoleRepository;
import com.lms.lmsbackend.auth.repository.UserRepository;
import com.lms.lmsbackend.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // =========================
    // SIGNUP
    // =========================
    public LoginResponse registerUser(SignupRequest request) {
        if (request == null || request.getEmail() == null || request.getPassword() == null
                || request.getEmail().isBlank() || request.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email & Password are required");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        // Default role: STUDENT
        String roleName = (request.getRole() == null || request.getRole().isBlank())
                ? "STUDENT"
                : request.getRole().toUpperCase();

        Role.ERole eRole;
        try {
            eRole = Role.ERole.valueOf(roleName);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role: " + roleName);
        }

        Role role = roleRepository.findByName(eRole)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Role not found in DB: " + eRole));

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setUsername(request.getUsername() != null && !request.getUsername().isBlank()
                ? request.getUsername()
                : request.getEmail());

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return LoginResponse.builder()
                .token(token)
                .role(role.getName().name())
                .userId(user.getId())
                .message("Signup successful")
                .build();
    }

    // =========================
    // LOGIN
    // =========================
    public LoginResponse loginUser(LoginRequest request) {
        if (request == null || request.getEmail() == null || request.getPassword() == null
                || request.getEmail().isBlank() || request.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email & Password are required");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        String token = jwtService.generateToken(user);

        return LoginResponse.builder()
                .token(token)
                .role(user.getRole().getName().name())
                .userId(user.getId())
                .message("Login successful")
                .build();
    }
}