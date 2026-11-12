package org.example.placement_drive_management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationRoundDto {
    Long id;
    String status;
    Double score;
    String feedback;
    Long applicationId;
    Long driveRoundId;
}
