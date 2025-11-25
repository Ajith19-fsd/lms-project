package com.lms.lmsbackend.auth.security;

import com.lms.lmsbackend.auth.model.Role;
import com.lms.lmsbackend.auth.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.mockito.Mockito.*;

class JwtAuthFilterTest {

    private JwtAuthFilter jwtAuthFilter;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtAuthFilter = new JwtAuthFilter(jwtService, userDetailsService);

        testUser = new User();
        testUser.setEmail("ajith@example.com");
        testUser.setUsername("ajith");
        Role role = new Role();
        role.setName(Role.ERole.STUDENT);
        testUser.setRole(role);
    }

    @Test
    void testDoFilterInternal_ValidToken() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/student/test");
        when(request.getHeader("Authorization")).thenReturn("Bearer mockToken");

        // Updated method names from JwtService
        when(jwtService.extractUsernameFromToken("mockToken")).thenReturn("ajith");

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("ajith")).thenReturn(mockUserDetails);
        when(jwtService.validateToken("mockToken", mockUserDetails)).thenReturn(true);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(userDetailsService, times(1)).loadUserByUsername("ajith");
        verify(jwtService, times(1)).validateToken("mockToken", mockUserDetails);
    }

    @Test
    void testDoFilterInternal_NoAuthHeader() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/student/test");
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void testDoFilterInternal_NonApiPath() throws Exception {
        when(request.getRequestURI()).thenReturn("/index.html");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
    }
}