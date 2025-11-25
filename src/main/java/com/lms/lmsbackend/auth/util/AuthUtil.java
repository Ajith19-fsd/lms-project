package com.lms.lmsbackend.auth.util;

import com.lms.lmsbackend.auth.model.User;
import com.lms.lmsbackend.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUtil {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;

        Object principal = auth.getPrincipal();

        if (principal instanceof User) return (User) principal;

        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            return userRepository.findByEmail(email).orElse(null);
        }

        return userRepository.findByEmail(auth.getName()).orElse(null);
    }

    public Long getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    public String getCurrentUserRole() {
        User user = getCurrentUser();
        return user != null && user.getRole() != null && user.getRole().getName() != null
                ? user.getRole().getName().name()
                : null;
    }

    public boolean hasRole(String roleName) {
        String currentRole = getCurrentUserRole();
        return currentRole != null && currentRole.equalsIgnoreCase(roleName);
    }
}