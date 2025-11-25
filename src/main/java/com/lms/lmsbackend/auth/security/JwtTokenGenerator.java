package com.lms.lmsbackend.auth.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenGenerator implements CommandLineRunner {

    private final JwtService jwtService;

    public JwtTokenGenerator(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("JWT Token Generator initialized...");
    }
}