package com.lms.lmsbackend.auth.repository;

import com.lms.lmsbackend.auth.model.User;
import com.lms.lmsbackend.auth.model.Role.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role.name = :roleName")
    long countByRoleName(@Param("roleName") ERole roleName);

    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);
}