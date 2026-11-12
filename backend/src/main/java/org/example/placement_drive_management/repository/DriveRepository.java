package org.example.placement_drive_management.repository;

import org.example.placement_drive_management.entity.Company;
import org.example.placement_drive_management.entity.Drive;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface DriveRepository extends JpaRepository<Drive,Long> {
    Optional<Drive> findByDriveId(String driveId);
    Boolean existsByDriveId(String driveId);
    Page<Drive> findByCompany_CompanyId(String companyId, Pageable pageable);
    List<Drive> findByCompany_CompanyId(String companyId);
    List<Drive> findAllByIsActive(Boolean active);
    long countByIsActive(Boolean active);
    long countByCompany_CompanyId(String companyId);
    long countByCompany_CompanyIdAndIsActive(String companyId, Boolean active);
}
