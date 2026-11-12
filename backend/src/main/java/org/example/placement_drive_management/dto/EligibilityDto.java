package org.example.placement_drive_management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EligibilityDto {
   private Long id;
    private Double minimumCgpa;
    private Integer maxActiveBacklogs;
    private String allowedBranch;
    private Integer passingYear;
    private String gender;
    private Boolean hasHistoryBacklogs;
    private String driveId;
}
