package org.example.placement_drive_management.mappers;

import org.example.placement_drive_management.dto.EligibilityDto;
import org.example.placement_drive_management.entity.Eligibility;

public class EligibilityMapper {
    public  static EligibilityDto mapToEligibilityDto(Eligibility eligibility){
        return  new EligibilityDto(
                eligibility.getId(),
                eligibility.getMinimumCgpa(),
                eligibility.getMaxActiveBacklogs(),
                eligibility.getAllowedBranch(),
                eligibility.getPassingYear(),
                eligibility.getGender(),
                eligibility.getHasHistoryBacklogs(),
                eligibility.getDrive().getDriveId()
        );
    }


}
