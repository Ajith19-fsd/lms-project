package com.lms.lmsbackend.auth.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    private final AuthenticationEntryPoint restAuthenticationEntryPoint = (request, response, authException) -> {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String body = "{\"timestamp\":\"" + java.time.OffsetDateTime.now() +
                "\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Authentication required\",\"path\":\"" +
                request.getRequestURI() + "\"}";
        response.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .csrf(cs -> cs.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e.authenticationEntryPoint(restAuthenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        // Public pages and static assets
                        .requestMatchers(HttpMethod.GET,
                                "/", "/index", "/login", "/signup",
                                "/redirect-page", "/redirectAfterLogin", "/error",
                                "/css/**", "/js/**", "/images/**", "/fonts/**", "/favicon.ico")
                        .permitAll()

                        // Auth APIs
                        .requestMatchers("/api/auth/**").permitAll()

                        // Swagger/OpenAPI
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger-resources/**").permitAll()

                        // API role-based protection
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/instructor/**").hasRole("INSTRUCTOR")
                        .requestMatchers("/api/student/**").hasRole("STUDENT")

                        // Thymeleaf pages: allow, JS fetches must use JWT
                        .requestMatchers("/admin/**", "/instructor/**", "/student/**").permitAll()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(h -> h.frameOptions(f -> f.disable()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("*"));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}