package com.patient_porta.repository;

import com.patient_porta.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    // US14.2
    List<User> findAllByRole(User.Role role);
    List<User> findAllByRoleAndStatusNot(User.Role role, User.Status status);
}
