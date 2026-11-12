package org.example.placement_drive_management.dto;

import lombok.*;

@Getter
@Setter @AllArgsConstructor @NoArgsConstructor
public class ApplicationRoundResponseDto {
    private Long id;
    private Long applicationId;
    // Student fields — denormalized for frontend
    private String studentRollNo;
    private String studentName;      // firstName + surname
    private String department;
    private Double currentCgpa;
    private String mobileNo;
    private String email;

    // Round result fields
    private String status;
    private Double score;
    private String feedback;
}
