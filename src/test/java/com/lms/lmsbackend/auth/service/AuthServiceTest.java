package com.lms.lmsbackend.auth.service;

import com.lms.lmsbackend.auth.dto.LoginRequest;
import com.lms.lmsbackend.auth.dto.LoginResponse;
import com.lms.lmsbackend.auth.dto.SignupRequest;
import com.lms.lmsbackend.auth.model.Role;
import com.lms.lmsbackend.auth.model.User;
import com.lms.lmsbackend.auth.repository.RoleRepository;
import com.lms.lmsbackend.auth.repository.UserRepository;
import com.lms.lmsbackend.auth.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private SignupRequest signupRequest;
    private LoginRequest loginRequest;
    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        role = new Role();
        role.setName(Role.ERole.STUDENT);

        user = new User();
        user.setId(1L);
        user.setFullName("Ajith Kumar");
        user.setUsername("ajith");
        user.setEmail("ajith@example.com");
        user.setPassword("encodedPassword");
        user.setRole(role);

        signupRequest = new SignupRequest();
        signupRequest.setFullName("Ajith Kumar");
        signupRequest.setUsername("ajith");
        signupRequest.setEmail("ajith@example.com");
        signupRequest.setPassword("password");
        signupRequest.setRole("STUDENT");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("ajith@example.com");
        loginRequest.setPassword("password");
    }

    @Test
    void testRegisterUser_Success() {
        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
        when(roleRepository.findByName(Role.ERole.STUDENT)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(jwtService.generateToken(any(User.class))).thenReturn("mockToken");

        LoginResponse response = authService.registerUser(signupRequest);

        assertNotNull(response);
        assertEquals("STUDENT", response.getRole());
        assertEquals(1L, response.getUserId());
        assertEquals("mockToken", response.getToken());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testLoginUser_Success() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("mockToken");

        LoginResponse response = authService.loginUser(loginRequest);

        assertNotNull(response);
        assertEquals("STUDENT", response.getRole());
        assertEquals(1L, response.getUserId());
        assertEquals("mockToken", response.getToken());
    }

    @Test
    void testLoginUser_InvalidPassword() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> authService.loginUser(loginRequest));
    }

    @Test
    void testRegisterUser_EmailExists() {
        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> authService.registerUser(signupRequest));
    }
}