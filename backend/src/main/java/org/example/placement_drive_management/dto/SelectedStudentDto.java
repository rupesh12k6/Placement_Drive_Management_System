package org.example.placement_drive_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SelectedStudentDto {

    // Only what company needs to contact and onboard the student

    private String name;
    private String email;
    private String phoneNumber;
    private String department;
    private Integer passingYear;
    // So company knows which role this student is selected for
    private String driveId;
    private String driveName;
    private String jobRole;
    private Double packageOffered;
    private String jobLocation;
}