package org.example.placement_drive_management.repository;

import jakarta.transaction.Transactional;
import org.example.placement_drive_management.entity.Applications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Applications, Long> {

    List<Applications> findByDrive_DriveId(String driveId);

    List<Applications> findByStudent_RollNo(String rollNo);
    Optional<Applications> findByDrive_DriveIdAndStudent_RollNo(
            String driveId,
            String rollNo
    );
    List<Applications> findByDrive_DriveIdAndStatus(String driveId, String status);
    // All applications for a student filtered by status
    List<Applications> findByStudent_RollNoAndStatus(String rollNo, String status);

    @Modifying
    @Transactional
    @Query("DELETE FROM Applications a WHERE a.drive.driveId = :driveId")
    void deleteByDrive_DriveId(@Param("driveId") String driveId);
    boolean existsByStudent_RollNoAndDrive_Company_CompanyId(
            String rollNo, String companyId);
}