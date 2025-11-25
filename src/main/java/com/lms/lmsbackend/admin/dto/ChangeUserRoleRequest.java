package com.lms.lmsbackend.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeUserRoleRequest {
    private Long userId;
    private String role;
}