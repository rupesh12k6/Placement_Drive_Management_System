package org.example.placement_drive_management.mappers;

import org.example.placement_drive_management.dto.ApplicationRoundDto;
import org.example.placement_drive_management.entity.ApplicationRound;

public class ApplicationRoundMapper {
    public static ApplicationRoundDto maptoApplicationRoundDto(ApplicationRound applicationRound) {
        return new ApplicationRoundDto(
                applicationRound.getId(),
                applicationRound.getStatus(),
                applicationRound.getScore(),
                applicationRound.getFeedback(),
                applicationRound.getApplication().getId(),
                applicationRound.getDriveRound().getId()
        );
    }

}
