package org.example.placement_drive_management.repository;

import org.example.placement_drive_management.dto.DriveRoundDto;
import org.example.placement_drive_management.entity.Applications;
import org.example.placement_drive_management.entity.Drive;
import org.example.placement_drive_management.entity.DriveRound;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DriveRoundRepository extends JpaRepository<DriveRound, Long> {
    List<DriveRound> findByDrive_DriveId(String driveId);
    // Fetch the specific round definition for a drive + round number
    Optional<DriveRound> findByDrive_DriveIdAndRoundNumber(String driveId, Integer roundNumber);
    boolean existsByDriveAndRoundNumber(Drive drive, Integer roundNumber);
    Boolean existsByDrive_DriveIdAndRoundNumber(String driveId,Integer roundNumber);
    Integer findMaxRoundNumberByDriveId(String driveId);
}
