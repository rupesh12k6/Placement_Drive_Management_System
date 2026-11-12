package org.example.placement_drive_management.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriveInfoDto {
    private String companyName;
    private String driveName;
    private String role;
    private Double packageAmount;
    private String jobLocation;
    private LocalDate registrationEndDate;
}
