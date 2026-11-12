package org.example.placement_drive_management.dto;

import lombok.AllArgsConstructor;
import lombok.*;
import org.example.placement_drive_management.entity.Applications;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentProfileDto {
        private Long id;
        private String department;
        private Double tenthPercentage;
        private Double twelthPercentage;
        private Double diplomaPercentage;
        private Double currentCgpa;
        private Integer currentSemester;
        private Integer backlogCount;
        private Boolean hasBacklogHistory;
        private Integer passingYear;
        private String gender;
        private String resumeUrl;

}
