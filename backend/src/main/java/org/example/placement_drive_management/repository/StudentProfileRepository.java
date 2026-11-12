package org.example.placement_drive_management.repository;

import org.example.placement_drive_management.entity.Student;
import org.example.placement_drive_management.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentProfileRepository extends JpaRepository<StudentProfile,Long> {
    Optional<StudentProfile> findByStudentRollNo(String rollNo);
    boolean existsByStudent(Student student);

    @Query("SELECT sp FROM student_profile sp WHERE sp.department IN :branches " +
            "AND sp.passingYear = :year " +
            "AND sp.currentCgpa >= :minCgpa " +
            "AND sp.backlogCount <= :maxBacklogs " +
            "AND (:gender = 'BOTH' OR sp.gender = :gender) " +
            "AND (:hasHistory = true OR sp.hasbackloghistory = false)")
    List<StudentProfile> findEligibleStudents(
            @Param("branches") List<String> branches,
            @Param("year") Integer year,
            @Param("minCgpa") Double minCgpa,
            @Param("maxBacklogs") Integer maxBacklogs,
            @Param("gender") String gender,
            @Param("hasHistory") Boolean hasHistory
    );
}
