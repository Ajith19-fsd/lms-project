package com.lms.lmsbackend.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    private String fullName;
    private String username;
    private String email;
    private String password;
    private String role;
}