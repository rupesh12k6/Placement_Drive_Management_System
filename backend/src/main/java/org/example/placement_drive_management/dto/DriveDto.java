package org.example.placement_drive_management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DriveDto {
    private Long id;
    private String driveId;
    private String driveName;
    private String jobRole;
    private Double packageOffered;
    private String jobLocation;
    private Integer vacancies;
    private LocalDate registrationStartDate;
    private LocalDate registrationEndDate;
    private Integer maxRounds;
    private Boolean isActive=false;
    private String externalLink;
    private String companyId;
    private String description;

}
