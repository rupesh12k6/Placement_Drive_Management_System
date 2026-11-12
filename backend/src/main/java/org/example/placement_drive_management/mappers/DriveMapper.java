package org.example.placement_drive_management.mappers;

import org.example.placement_drive_management.dto.DriveDto;
import org.example.placement_drive_management.entity.Drive;

import java.util.ArrayList;

public class DriveMapper {
    public static DriveDto maptoDriveDto(Drive drive) {
        return new DriveDto(
                drive.getId(),
                drive.getDriveId(),
                drive.getDriveName(),
                drive.getJobRole(),
                drive.getPackageOffered(),
                drive.getJobLocation(),
                drive.getVacancies(),
                drive.getRegistrationStartDate(),
                drive.getRegistrationEndDate(),
                drive.getMaxRounds(),
                drive.getIsActive(),
                drive.getExternalLink(),
                drive.getCompany().getCompanyId(),
                drive.getDescription()
        );
    }

    public static Drive maptoDrive(DriveDto driveDto) {
        return new Drive(driveDto.getId(),
                driveDto.getDriveId(),
                driveDto.getDriveName(),
                driveDto.getJobRole(),
                driveDto.getPackageOffered(),
                driveDto.getJobLocation(),
                driveDto.getVacancies(),
                driveDto.getRegistrationStartDate(),
                driveDto.getRegistrationEndDate(),
                driveDto.getMaxRounds(),
                false,
                driveDto.getExternalLink(),
                null,
                driveDto.getDescription(),
                null,
                new ArrayList<>(),
                new ArrayList<>()

        );
    }
}
