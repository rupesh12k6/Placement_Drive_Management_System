package org.example.placement_drive_management.mappers;

import org.example.placement_drive_management.dto.DriveRoundDto;
import org.example.placement_drive_management.entity.DriveRound;

public class DriveRoundMapper {
    public static DriveRoundDto mapToDriveRoundDto(DriveRound driveRound) {
        return new DriveRoundDto(
                driveRound.getId(),
                driveRound.getRoundNumber(),
                driveRound.getRoundName(),
                driveRound.getRoundDate(),
                driveRound.getRoundLink()
        );
    }
    public static DriveRound mapToDriveRound(DriveRoundDto driveRoundDto) {
        return new DriveRound(
                driveRoundDto.getId(),
                driveRoundDto.getRoundNumber(),
                driveRoundDto.getRoundName(),
                driveRoundDto.getRoundDate(),
                driveRoundDto.getRoundLink(),
                null,
                null
        );
    }
}
