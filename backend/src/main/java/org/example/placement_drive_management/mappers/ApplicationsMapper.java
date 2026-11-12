package org.example.placement_drive_management.mappers;

import org.example.placement_drive_management.dto.ApplicationsDto;
import org.example.placement_drive_management.entity.Applications;

public class ApplicationsMapper {
    public static ApplicationsDto mapToApplicationDto(Applications applications) {
        ApplicationsDto applicationsDto = new ApplicationsDto();
        applicationsDto.setId(applications.getId());
        applicationsDto.setStudentRollNo(applications.getStudent().getRollNo());
        applicationsDto.setDriveId(applications.getDrive().getDriveId());
        applicationsDto.setStatus(applications.getStatus());
        applicationsDto.setCurrentRound(applications.getCurrentRoundNumber());
        applicationsDto.setAppliedAt(applications.getAppliedDate());
        applicationsDto.setOfferAccepted(false);
        return applicationsDto;
    }
}
