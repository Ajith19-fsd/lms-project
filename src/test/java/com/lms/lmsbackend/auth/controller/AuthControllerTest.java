package com.lms.lmsbackend.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.lmsbackend.auth.dto.LoginRequest;
import com.lms.lmsbackend.auth.dto.LoginResponse;
import com.lms.lmsbackend.auth.dto.SignupRequest;
import com.lms.lmsbackend.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Include simple global exception handler to map ResponseStatusException
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new Object() {
                    // Handle ResponseStatusException in tests
                    @org.springframework.web.bind.annotation.ExceptionHandler(ResponseStatusException.class)
                    public org.springframework.http.ResponseEntity<String> handle(ResponseStatusException ex) {
                        return org.springframework.http.ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
                    }
                })
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setFullName("Ajith Kumar");
        request.setUsername("ajith");
        request.setEmail("ajith@example.com");
        request.setPassword("password");
        request.setRole("STUDENT");

        LoginResponse response = LoginResponse.builder()
                .token("mockToken")
                .role("STUDENT")
                .userId(1L)
                .message("Signup successful")
                .build();

        when(authService.registerUser(any(SignupRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/signup/STUDENT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mockToken"))
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.message").value("Signup successful"));
    }

    @Test
    void testLoginUser_Success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("ajith@example.com");
        request.setPassword("password");

        LoginResponse response = LoginResponse.builder()
                .token("mockToken")
                .role("STUDENT")
                .userId(1L)
                .message("Login successful")
                .build();

        when(authService.loginUser(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mockToken"))
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    void testLoginUser_Failure() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("wrong@example.com");
        request.setPassword("wrongpass");

        when(authService.loginUser(any(LoginRequest.class)))
                .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid email or password"));
    }
}