package org.example.placement_drive_management.repository;

import org.example.placement_drive_management.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student,Long> {
    Optional<Student> findByRollNo(String rollNo);
    Optional<Student> getStudentById(Long id);
    Optional<Student> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByRollNo(String rollNo);
}
