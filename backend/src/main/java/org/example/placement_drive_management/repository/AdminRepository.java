package org.example.placement_drive_management.repository;

import org.example.placement_drive_management.entity.Admin;
import org.example.placement_drive_management.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin,Long> {
    Optional<Admin> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByRole(Role role);
    long countByRoleNot(Role role);
    Optional<Admin> findById(Long id);
}
