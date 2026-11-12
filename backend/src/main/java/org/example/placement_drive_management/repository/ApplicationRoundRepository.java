package org.example.placement_drive_management.repository;

import org.example.placement_drive_management.entity.ApplicationRound;
import org.example.placement_drive_management.service.ApplicationRoundProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplicationRoundRepository extends JpaRepository<ApplicationRound, Long> {
    @Query("""
    SELECT 
        a.id as applicationId,
        s.rollNo as studentRollNo,
        CONCAT(s.name, ' ', s.surname) as studentName,
        sp.department as department,
        sp.currentCgpa as currentCgpa,
        s.mobileNo as mobileNo,
        s.email as email,
        ar.status as status,
        ar.score as score,
        ar.feedback as feedback,
        sp.resumeUrl as resume
    FROM applicationRound ar
    JOIN ar.application a
    JOIN a.student s
    JOIN a.studentProfile sp
    JOIN ar.driveRound dr
    WHERE dr.drive.driveId = :driveId
    AND dr.roundNumber = :roundNo
""")
    List<ApplicationRoundProjection> findApplicantsProjected(
            @Param("driveId") String driveId,
            @Param("roundNo") Integer roundNo);


    List<ApplicationRound> findByDriveRound_RoundNumber(Integer roundNumber);
    Optional<ApplicationRound> findByApplication_IdAndDriveRound_RoundNumber(
            Long applicationId, Integer roundNumber);

    @Query("SELECT ar FROM applicationRound ar " +
            "WHERE ar.driveRound.drive.driveId = :driveId " +
            "AND ar.driveRound.roundNumber = :roundNo " +
            "AND ar.score IS NOT NULL " +
            "ORDER BY ar.score DESC")
    List<ApplicationRound> findScoredStudentsOrderByScore(
            @Param("driveId") String driveId,
            @Param("roundNo") Integer roundNo);

    @Query("SELECT COUNT(ar) FROM applicationRound ar " +
            "WHERE ar.driveRound.drive.driveId = :driveId " +
            "AND ar.driveRound.roundNumber = :roundNo " +
            "AND ar.score >= :cutOff")
    long countScoredStudentsAboveCutOff(
            @Param("driveId") String driveId,
            @Param("roundNo") Integer roundNo,
            @Param("cutOff") Double cutOff);

    @Query("SELECT ar FROM applicationRound ar " +
            "JOIN ar.application a " +
            "JOIN a.student s " +
            "WHERE a.drive.driveId = :driveId " +
            "AND s.rollNo = :rollNo " +
            "AND ar.driveRound.roundNumber = :roundNo")
    Optional<ApplicationRound> findByDriveIdAndRollNoAndRoundNo(
            @Param("driveId") String driveId,
            @Param("rollNo") String rollNo,
            @Param("roundNo") Integer roundNo);

    List<ApplicationRound> findByApplicationId(Long id);

    @Query("""
    SELECT ar
    FROM applicationRound ar
    JOIN FETCH ar.application a
    JOIN FETCH a.student s
    JOIN FETCH ar.driveRound dr
    WHERE dr.drive.driveId = :driveId
    AND s.rollNo = :rollNo
""")
    List<ApplicationRound> findAllRoundDetails(
            @Param("driveId") String driveId,
            @Param("rollNo") String rollNo
    );
}
