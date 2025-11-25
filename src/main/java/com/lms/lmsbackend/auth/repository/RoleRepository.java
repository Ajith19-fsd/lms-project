package com.lms.lmsbackend.auth.repository;

import com.lms.lmsbackend.auth.model.Role;
import com.lms.lmsbackend.auth.model.Role.ERole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}