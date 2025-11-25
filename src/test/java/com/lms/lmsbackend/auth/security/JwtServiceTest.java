package com.lms.lmsbackend.auth.security;

import com.lms.lmsbackend.auth.model.Role;
import com.lms.lmsbackend.auth.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();

        // Inject secretKey
        Field secretField = JwtService.class.getDeclaredField("secretKey");
        secretField.setAccessible(true);
        secretField.set(jwtService, "bXlTZWNyZXRLZXlGb3JKd3RUb2tlbjEyMzQ1Njc4OTA=");

        // Inject expirationMs
        Field expField = JwtService.class.getDeclaredField("expirationMs");
        expField.setAccessible(true);
        expField.set(jwtService, 86400000L);
    }

    @Test
    void testGenerateTokenAndExtractUsername() {
        Role role = new Role();
        role.setName(Role.ERole.STUDENT);

        User user = new User();
        user.setUsername("ajith");
        user.setEmail("ajith@example.com");
        user.setRole(role);

        String token = jwtService.generateToken(user);
        assertNotNull(token);

        String username = jwtService.extractUsernameFromToken(token);
        assertEquals("ajith", username);
    }

    @Test
    void testValidateToken() {
        Role role = new Role();
        role.setName(Role.ERole.INSTRUCTOR);

        User user = new User();
        user.setUsername("instructor");
        user.setEmail("instructor@example.com");
        user.setRole(role);

        String token = jwtService.generateToken(user);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("instructor");

        assertTrue(jwtService.validateToken(token, userDetails));

        when(userDetails.getUsername()).thenReturn("wrongUser");
        assertFalse(jwtService.validateToken(token, userDetails));
    }
}