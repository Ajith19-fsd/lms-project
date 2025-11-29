package com.lms.lmsbackend.admin.temp;

import com.lms.lmsbackend.auth.model.Role;
import com.lms.lmsbackend.auth.model.User;
import com.lms.lmsbackend.auth.repository.UserRepository;
import com.lms.lmsbackend.auth.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

@Controller
public class AdminTempSignupController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Show Temporary Admin Signup page
    @GetMapping("/admin/temp/signup")
    public String showAdminSignupPage() {
        return "admin-temp-signup";
    }

    // Handle Temporary Admin Signup
    @PostMapping("/admin/temp/signup")
    public String registerAdmin(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            Model model
    ) {
        try {
            if (userRepository.existsByEmail(email)) {
                model.addAttribute("error", "Email already exists");
                return "admin-temp-signup";
            }

            // Get ADMIN role
            Role adminRole = roleRepository.findByName(Role.ERole.ADMIN)
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

            // Create new User object
            User user = new User();
            user.setFullName(name);
            user.setUsername(name); // set username same as full name
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(adminRole);

            // Save to DB
            userRepository.save(user);

            model.addAttribute("success", "Admin created successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Error creating admin: " + e.getMessage());
        }

        return "admin-temp-signup";
    }
}
