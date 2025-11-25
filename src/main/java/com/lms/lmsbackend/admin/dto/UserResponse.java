package com.lms.lmsbackend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;  // fullName or username
    private String email;
    private String role;
}