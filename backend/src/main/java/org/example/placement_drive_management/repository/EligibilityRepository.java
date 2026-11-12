package org.example.placement_drive_management.repository;

import org.example.placement_drive_management.dto.EligibilityDto;
import org.example.placement_drive_management.entity.Drive;
import org.example.placement_drive_management.entity.Eligibility;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EligibilityRepository extends JpaRepository<Eligibility,Long> {
    Optional<Eligibility> findByDrive_DriveId(String drive);
    Boolean existsByDrive_DriveId(String driveId);
}
